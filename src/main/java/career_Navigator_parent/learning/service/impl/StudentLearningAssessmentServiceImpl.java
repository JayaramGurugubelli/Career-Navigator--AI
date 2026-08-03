package career_Navigator_parent.learning.service.impl;

import career_Navigator_parent.learning.dto.request.StudentLearningRequests;
import career_Navigator_parent.learning.dto.response.LearningResponses;
import career_Navigator_parent.learning.entity.Assessment;
import career_Navigator_parent.learning.entity.AssessmentOption;
import career_Navigator_parent.learning.entity.AssessmentQuestion;
import career_Navigator_parent.learning.entity.StudentAssessmentAnswer;
import career_Navigator_parent.learning.entity.StudentAssessmentAttempt;
import career_Navigator_parent.learning.enums.AssessmentAttemptStatus;
import career_Navigator_parent.learning.enums.AssessmentStatus;
import career_Navigator_parent.learning.mapper.LearningMapper;
import career_Navigator_parent.learning.repository.AssessmentOptionRepository;
import career_Navigator_parent.learning.repository.AssessmentQuestionRepository;
import career_Navigator_parent.learning.repository.AssessmentLearningRepository;
import career_Navigator_parent.learning.repository.StudentAssessmentAttemptRepository;
import career_Navigator_parent.learning.service.StudentLearningAssessmentService;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentLearningAssessmentServiceImpl
        implements StudentLearningAssessmentService {

    private final AssessmentLearningRepository assessmentLearningRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;
    private final AssessmentOptionRepository assessmentOptionRepository;
    private final StudentAssessmentAttemptRepository attemptRepository;
    private final StudentRepository studentRepository;
    private final SecurityUtils securityUtils;
    private final LearningMapper learningMapper;

    @Override
    public LearningResponses.AssessmentAttempt start(
            Long assessmentId
    ) {
        Student student = getCurrentStudent();

        Assessment assessment =
                assessmentLearningRepository
                        .findDetailedById(assessmentId)
                        .filter(item ->
                                item.getStatus()
                                        == AssessmentStatus.PUBLISHED
                                        && Boolean.TRUE.equals(
                                        item.getActive()
                                )
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Published assessment not found."
                                )
                        );

        long usedAttempts =
                attemptRepository
                        .countByStudentIdAndAssessmentId(
                                student.getId(),
                                assessmentId
                        );

        if (
                usedAttempts
                        >= assessment.getMaximumAttempts()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Maximum assessment attempts reached."
            );
        }

        StudentAssessmentAttempt attempt =
                StudentAssessmentAttempt.builder()
                        .student(student)
                        .assessment(assessment)
                        .attemptNumber(
                                Math.toIntExact(
                                        usedAttempts + 1
                                )
                        )
                        .status(
                                AssessmentAttemptStatus.STARTED
                        )
                        .passed(false)
                        .correctAnswers(0)
                        .wrongAnswers(0)
                        .unansweredQuestions(0)
                        .startedAt(
                                LocalDateTime.now()
                        )
                        .build();

        return learningMapper.toAttempt(
                attemptRepository.save(attempt)
        );
    }

    @Override
    public LearningResponses.AssessmentAttempt submit(
            Long attemptId,
            StudentLearningRequests.SubmitAssessment request
    ) {
        Student student = getCurrentStudent();

        StudentAssessmentAttempt attempt =
                attemptRepository
                        .findByIdAndStudentId(
                                attemptId,
                                student.getId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Assessment attempt not found."
                                )
                        );

        if (attempt.isExpired()) {
            attempt.setStatus(
                    AssessmentAttemptStatus.EXPIRED
            );

            attemptRepository.save(attempt);

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Assessment attempt has expired."
            );
        }

        if (
                attempt.getStatus()
                        != AssessmentAttemptStatus.STARTED
                        && attempt.getStatus()
                        != AssessmentAttemptStatus.IN_PROGRESS
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Assessment attempt cannot be submitted in its current state."
            );
        }

        Assessment assessment =
                attempt.getAssessment();

        List<AssessmentQuestion> questions =
                assessmentQuestionRepository
                        .findByAssessmentIdAndActiveTrueOrderBySequenceNumberAsc(
                                assessment.getId()
                        );

        Map<Long, StudentLearningRequests.AssessmentAnswer>
                submittedAnswers =
                request.answers()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        StudentLearningRequests
                                                .AssessmentAnswer
                                                ::questionId,
                                        answer -> answer,
                                        (
                                                first,
                                                duplicate
                                        ) -> {
                                            throw new ResponseStatusException(
                                                    HttpStatus.BAD_REQUEST,
                                                    "Duplicate answer for question ID "
                                                            + first.questionId()
                                            );
                                        }
                                )
                        );

        int correctAnswers = 0;
        int wrongAnswers = 0;
        int unansweredQuestions = 0;
        double awardedScore = 0.0;

        attempt.getAnswers().clear();

        for (AssessmentQuestion question : questions) {

            StudentLearningRequests.AssessmentAnswer
                    submittedAnswer =
                    submittedAnswers.get(
                            question.getId()
                    );

            if (submittedAnswer == null) {
                unansweredQuestions++;
                continue;
            }

            boolean correct =
                    evaluate(
                            question,
                            submittedAnswer
                    );

            double marks =
                    correct
                            ? question.getMarks()
                            : 0.0;

            Set<AssessmentOption> selectedOptions =
                    resolveSelectedOptions(
                            question,
                            submittedAnswer
                                    .selectedOptionIds()
                    );

            StudentAssessmentAnswer answer =
                    StudentAssessmentAnswer.builder()
                            .attempt(attempt)
                            .question(question)
                            .selectedOptions(
                                    selectedOptions
                            )
                            .textAnswer(
                                    submittedAnswer
                                            .textAnswer()
                            )
                            .numericAnswer(
                                    submittedAnswer
                                            .numericAnswer()
                            )
                            .fileUrl(
                                    submittedAnswer
                                            .fileUrl()
                            )
                            .correct(correct)
                            .marksAwarded(marks)
                            .answeredAt(
                                    LocalDateTime.now()
                            )
                            .evaluatedAt(
                                    LocalDateTime.now()
                            )
                            .build();

            attempt.addAnswer(answer);

            awardedScore += marks;

            if (correct) {
                correctAnswers++;
            } else {
                wrongAnswers++;
            }
        }

        attempt.submit();

        attempt.completeEvaluation(
                awardedScore,
                assessment.getMaximumScore(),
                correctAnswers,
                wrongAnswers,
                unansweredQuestions
        );

        return learningMapper.toAttempt(
                attemptRepository.save(attempt)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LearningResponses.AssessmentAttempt get(
            Long attemptId
    ) {
        Student student = getCurrentStudent();

        StudentAssessmentAttempt attempt =
                attemptRepository
                        .findByIdAndStudentId(
                                attemptId,
                                student.getId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Assessment attempt not found."
                                )
                        );

        return learningMapper.toAttempt(attempt);
    }

    private boolean evaluate(
            AssessmentQuestion question,
            StudentLearningRequests.AssessmentAnswer answer
    ) {
        return switch (
                question.getQuestionType()
                ) {
            case SINGLE_CHOICE,
                 MULTIPLE_CHOICE ->
                    evaluateOptions(
                            question,
                            answer.selectedOptionIds()
                    );

            case TRUE_FALSE,
                 DESCRIPTIVE ->
                    evaluateText(
                            question,
                            answer.textAnswer()
                    );

            case NUMERIC ->
                    evaluateNumeric(
                            question,
                            answer.numericAnswer()
                    );

            case FILE_UPLOAD -> false;
        };
    }

    private boolean evaluateOptions(
            AssessmentQuestion question,
            Set<Long> selectedOptionIds
    ) {
        Set<Long> correctOptionIds =
                question.getOptions()
                        .stream()
                        .filter(option ->
                                Boolean.TRUE.equals(
                                        option.getCorrectOption()
                                )
                        )
                        .map(
                                AssessmentOption::getId
                        )
                        .collect(
                                Collectors.toSet()
                        );

        Set<Long> actualOptionIds =
                selectedOptionIds == null
                        ? Set.of()
                        : Set.copyOf(
                        selectedOptionIds
                );

        return correctOptionIds.equals(
                actualOptionIds
        );
    }

    private boolean evaluateText(
            AssessmentQuestion question,
            String answer
    ) {
        if (
                question.getExpectedAnswer() == null
                        || answer == null
        ) {
            return false;
        }

        String expected =
                question.getExpectedAnswer()
                        .strip();

        String actual =
                answer.strip();

        if (
                Boolean.TRUE.equals(
                        question.getCaseSensitive()
                )
        ) {
            return expected.equals(actual);
        }

        return expected.equalsIgnoreCase(actual);
    }

    private boolean evaluateNumeric(
            AssessmentQuestion question,
            Double answer
    ) {
        if (
                question.getExpectedAnswer() == null
                        || answer == null
        ) {
            return false;
        }

        double expected;

        try {
            expected =
                    Double.parseDouble(
                            question.getExpectedAnswer()
                                    .strip()
                    );
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Assessment numeric answer configuration is invalid."
            );
        }

        double tolerance =
                question.getNumericTolerance() == null
                        ? 0.0
                        : question.getNumericTolerance();

        return Math.abs(
                expected - answer
        ) <= tolerance;
    }

    private Set<AssessmentOption> resolveSelectedOptions(
            AssessmentQuestion question,
            Set<Long> optionIds
    ) {
        if (
                optionIds == null
                        || optionIds.isEmpty()
        ) {
            return new LinkedHashSet<>();
        }

        Set<AssessmentOption> options =
                new LinkedHashSet<>(
                        assessmentOptionRepository
                                .findAllById(optionIds)
                );

        boolean containsInvalidOption =
                options.stream()
                        .anyMatch(option ->
                                !option.getQuestion()
                                        .getId()
                                        .equals(
                                                question.getId()
                                        )
                        );

        if (
                containsInvalidOption
                        || options.size()
                        != optionIds.size()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "One or more selected options are invalid."
            );
        }

        return options;
    }

    private Student getCurrentStudent() {
        Long userId =
                securityUtils.getCurrentUserId();

        return studentRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Student profile not found."
                        )
                );
    }
}