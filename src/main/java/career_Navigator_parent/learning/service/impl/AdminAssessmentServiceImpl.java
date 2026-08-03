package career_Navigator_parent.learning.service.impl;

import career_Navigator_parent.learning.dto.request.AdminLearningRequests;
import career_Navigator_parent.learning.dto.request.LearningStatusRequests;
import career_Navigator_parent.learning.dto.response.LearningPublicationResponses;
import career_Navigator_parent.learning.entity.Assessment;
import career_Navigator_parent.learning.entity.AssessmentOption;
import career_Navigator_parent.learning.entity.AssessmentQuestion;
import career_Navigator_parent.learning.entity.Course;
import career_Navigator_parent.learning.entity.LearningPathMilestone;
import career_Navigator_parent.learning.enums.AssessmentStatus;
import career_Navigator_parent.learning.enums.QuestionType;
import career_Navigator_parent.learning.repository.AssessmentLearningRepository;
import career_Navigator_parent.learning.repository.AssessmentQuestionRepository;
import career_Navigator_parent.learning.repository.CourseRepository;
import career_Navigator_parent.learning.repository.LearningPathMilestoneRepository;
import career_Navigator_parent.learning.service.AdminAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminAssessmentServiceImpl
        implements AdminAssessmentService {

    private final AssessmentLearningRepository assessmentRepository;
    private final AssessmentQuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final LearningPathMilestoneRepository milestoneRepository;

    @Override
    public Long create(
            AdminLearningRequests.AssessmentCreate request
    ) {
        Course course =
                request.courseId() == null
                        ? null
                        : courseRepository.findById(
                                request.courseId()
                        )
                        .orElseThrow(() ->
                                notFound(
                                        "Course not found."
                                )
                        );

        LearningPathMilestone milestone =
                request.milestoneId() == null
                        ? null
                        : milestoneRepository.findById(
                                request.milestoneId()
                        )
                        .orElseThrow(() ->
                                notFound(
                                        "Milestone not found."
                                )
                        );

        Assessment assessment =
                Assessment.builder()
                        .course(course)
                        .milestone(milestone)
                        .title(request.title())
                        .description(request.description())
                        .instructions(request.instructions())
                        .assessmentType(
                                request.assessmentType()
                        )
                        .status(AssessmentStatus.DRAFT)
                        .passingScore(
                                request.passingScore()
                        )
                        .maximumScore(
                                request.maximumScore()
                        )
                        .maximumAttempts(
                                request.maximumAttempts()
                        )
                        .durationMinutes(
                                request.durationMinutes()
                        )
                        .shuffleQuestions(
                                request.shuffleQuestions()
                        )
                        .shuffleOptions(
                                request.shuffleOptions()
                        )
                        .showResultImmediately(
                                request.showResultImmediately()
                        )
                        .showCorrectAnswers(
                                request.showCorrectAnswers()
                        )
                        .negativeMarkingEnabled(
                                request.negativeMarkingEnabled()
                        )
                        .negativeMarksPerWrongAnswer(
                                request.negativeMarksPerWrongAnswer()
                        )
                        .mandatory(request.mandatory())
                        .active(request.active())
                        .build();

        if (request.questions() != null) {
            for (
                    AdminLearningRequests.QuestionCreate questionRequest
                    : request.questions()
            ) {
                AssessmentQuestion question =
                        AssessmentQuestion.builder()
                                .assessment(assessment)
                                .questionText(
                                        questionRequest.questionText()
                                )
                                .questionContext(
                                        questionRequest.questionContext()
                                )
                                .imageUrl(
                                        questionRequest.imageUrl()
                                )
                                .questionType(
                                        questionRequest.questionType()
                                )
                                .difficulty(
                                        questionRequest.difficulty()
                                )
                                .sequenceNumber(
                                        questionRequest.sequenceNumber()
                                )
                                .marks(
                                        questionRequest.marks()
                                )
                                .negativeMarks(
                                        questionRequest.negativeMarks()
                                )
                                .expectedAnswer(
                                        questionRequest.expectedAnswer()
                                )
                                .answerExplanation(
                                        questionRequest.answerExplanation()
                                )
                                .caseSensitive(
                                        questionRequest.caseSensitive()
                                )
                                .numericTolerance(
                                        questionRequest.numericTolerance()
                                )
                                .active(true)
                                .build();

                if (questionRequest.options() != null) {
                    for (
                            AdminLearningRequests.OptionCreate optionRequest
                            : questionRequest.options()
                    ) {
                        question.addOption(
                                AssessmentOption.builder()
                                        .question(question)
                                        .optionText(
                                                optionRequest.optionText()
                                        )
                                        .imageUrl(
                                                optionRequest.imageUrl()
                                        )
                                        .sequenceNumber(
                                                optionRequest.sequenceNumber()
                                        )
                                        .correctOption(
                                                optionRequest.correctOption()
                                        )
                                        .active(true)
                                        .build()
                        );
                    }
                }

                assessment.addQuestion(question);
            }
        }

        return assessmentRepository
                .save(assessment)
                .getId();
    }

    @Override
    public LearningPublicationResponses.AssessmentStatusResponse updateStatus(
            Long assessmentId,
            LearningStatusRequests.AssessmentStatusUpdate request
    ) {
        Assessment assessment =
                assessmentRepository.findDetailedById(
                                assessmentId
                        )
                        .orElseThrow(() ->
                                notFound(
                                        "Assessment not found."
                                )
                        );

        if (request.status()
                == AssessmentStatus.PUBLISHED) {
            validateForPublication(assessment);
        }

        assessment.setStatus(request.status());

        Assessment saved =
                assessmentRepository.save(assessment);

        return new LearningPublicationResponses.AssessmentStatusResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getAssessmentType(),
                saved.getStatus(),
                saved.getActive(),
                saved.getCourse() == null
                        ? null
                        : saved.getCourse().getId(),
                saved.getMilestone() == null
                        ? null
                        : saved.getMilestone().getId(),
                questionRepository
                        .countByAssessmentIdAndActiveTrue(
                                saved.getId()
                        )
        );
    }

    private void validateForPublication(
            Assessment assessment
    ) {
        if (!Boolean.TRUE.equals(
                assessment.getActive()
        )) {
            throw conflict(
                    "Only an active assessment can be published."
            );
        }

        if (assessment.getCourse() == null
                && assessment.getMilestone() == null) {
            throw conflict(
                    "Assessment must belong to a course or milestone."
            );
        }

        List<AssessmentQuestion> questions =
                assessment.getQuestions()
                        .stream()
                        .filter(question ->
                                Boolean.TRUE.equals(
                                        question.getActive()
                                )
                        )
                        .toList();

        if (questions.isEmpty()) {
            throw conflict(
                    "Assessment requires at least one active question before publication."
            );
        }

        Set<Integer> sequenceNumbers =
                new HashSet<>();

        double configuredMaximumScore = 0.0;

        for (AssessmentQuestion question : questions) {
            if (!sequenceNumbers.add(
                    question.getSequenceNumber()
            )) {
                throw conflict(
                        "Assessment contains duplicate question sequence numbers."
                );
            }

            configuredMaximumScore +=
                    question.getMarks();

            validateQuestion(question);
        }

        if (assessment.getMaximumScore() == null
                || assessment.getMaximumScore() <= 0) {
            throw conflict(
                    "Assessment maximum score must be greater than zero."
            );
        }

        if (Math.abs(
                configuredMaximumScore
                        - assessment.getMaximumScore()
        ) > 0.0001) {
            throw conflict(
                    "Assessment maximum score must equal the sum of active question marks."
            );
        }

        if (assessment.getPassingScore() == null
                || assessment.getPassingScore() < 0
                || assessment.getPassingScore()
                > assessment.getMaximumScore()) {
            throw conflict(
                    "Assessment passing score must be between zero and maximum score."
            );
        }
    }

    private void validateQuestion(
            AssessmentQuestion question
    ) {
        QuestionType questionType =
                question.getQuestionType();

        if (questionType
                == QuestionType.SINGLE_CHOICE) {
            long correctOptions =
                    question.getOptions()
                            .stream()
                            .filter(option ->
                                    Boolean.TRUE.equals(
                                            option.getActive()
                                    )
                            )
                            .filter(option ->
                                    Boolean.TRUE.equals(
                                            option.getCorrectOption()
                                    )
                            )
                            .count();

            if (correctOptions != 1) {
                throw conflict(
                        "A single-choice question must contain exactly one correct option."
                );
            }
        }

        if (questionType
                == QuestionType.MULTIPLE_CHOICE) {
            long activeOptions =
                    question.getOptions()
                            .stream()
                            .filter(option ->
                                    Boolean.TRUE.equals(
                                            option.getActive()
                                    )
                            )
                            .count();

            long correctOptions =
                    question.getOptions()
                            .stream()
                            .filter(option ->
                                    Boolean.TRUE.equals(
                                            option.getActive()
                                    )
                            )
                            .filter(option ->
                                    Boolean.TRUE.equals(
                                            option.getCorrectOption()
                                    )
                            )
                            .count();

            if (activeOptions < 2
                    || correctOptions < 1) {
                throw conflict(
                        "A multiple-choice question requires at least two active options and one correct option."
                );
            }
        }

        if ((questionType
                == QuestionType.TRUE_FALSE
                || questionType
                == QuestionType.NUMERIC
                || questionType
                == QuestionType.DESCRIPTIVE)
                && (question.getExpectedAnswer() == null
                || question.getExpectedAnswer().isBlank())) {
            throw conflict(
                    "Expected answer is required for true/false, numeric, and descriptive questions."
            );
        }
    }

    private ResponseStatusException notFound(
            String message
    ) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                message
        );
    }

    private ResponseStatusException conflict(
            String message
    ) {
        return new ResponseStatusException(
                HttpStatus.CONFLICT,
                message
        );
    }
}