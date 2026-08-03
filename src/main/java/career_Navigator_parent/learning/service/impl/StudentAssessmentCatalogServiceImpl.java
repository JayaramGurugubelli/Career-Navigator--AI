package career_Navigator_parent.learning.service.impl;

import career_Navigator_parent.learning.dto.response.StudentAssessmentViewResponses;
import career_Navigator_parent.learning.entity.Assessment;
import career_Navigator_parent.learning.entity.AssessmentOption;
import career_Navigator_parent.learning.entity.AssessmentQuestion;
import career_Navigator_parent.learning.enums.AssessmentStatus;
import career_Navigator_parent.learning.repository.AssessmentLearningRepository;
import career_Navigator_parent.learning.repository.AssessmentQuestionRepository;
import career_Navigator_parent.learning.service.StudentAssessmentCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentAssessmentCatalogServiceImpl
        implements StudentAssessmentCatalogService {

    private final AssessmentLearningRepository assessmentRepository;
    private final AssessmentQuestionRepository questionRepository;

    @Override
    public StudentAssessmentViewResponses.AssessmentView
    getPublishedAssessment(
            Long assessmentId
    ) {
        Assessment assessment =
                assessmentRepository
                        .findPublishedDetailedById(
                                assessmentId,
                                AssessmentStatus.PUBLISHED
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Published assessment not found."
                                )
                        );

        List<AssessmentQuestion> sourceQuestions =
                new ArrayList<>(
                        questionRepository
                                .findByAssessmentIdAndActiveTrueOrderBySequenceNumberAsc(
                                        assessmentId
                                )
                );

        if (Boolean.TRUE.equals(
                assessment.getShuffleQuestions()
        )) {
            Collections.shuffle(sourceQuestions);
        }

        List<StudentAssessmentViewResponses.Question>
                questions =
                sourceQuestions.stream()
                        .map(question ->
                                mapQuestion(
                                        assessment,
                                        question
                                )
                        )
                        .toList();

        return new StudentAssessmentViewResponses.AssessmentView(
                assessment.getId(),
                assessment.getTitle(),
                assessment.getDescription(),
                assessment.getInstructions(),
                assessment.getAssessmentType(),
                assessment.getPassingScore(),
                assessment.getMaximumScore(),
                assessment.getMaximumAttempts(),
                assessment.getDurationMinutes(),
                assessment.getShuffleQuestions(),
                assessment.getShuffleOptions(),
                assessment.getCourse() == null
                        ? null
                        : assessment.getCourse().getId(),
                assessment.getMilestone() == null
                        ? null
                        : assessment.getMilestone().getId(),
                questions
        );
    }

    private StudentAssessmentViewResponses.Question
    mapQuestion(
            Assessment assessment,
            AssessmentQuestion question
    ) {
        List<AssessmentOption> sourceOptions =
                new ArrayList<>(
                        question.getOptions()
                                .stream()
                                .filter(option ->
                                        Boolean.TRUE.equals(
                                                option.getActive()
                                        )
                                )
                                .toList()
                );

        if (Boolean.TRUE.equals(
                assessment.getShuffleOptions()
        )) {
            Collections.shuffle(sourceOptions);
        }

        List<StudentAssessmentViewResponses.Option>
                options =
                sourceOptions.stream()
                        .map(option ->
                                new StudentAssessmentViewResponses.Option(
                                        option.getId(),
                                        option.getOptionText(),
                                        option.getImageUrl(),
                                        option.getSequenceNumber()
                                )
                        )
                        .toList();

        return new StudentAssessmentViewResponses.Question(
                question.getId(),
                question.getQuestionText(),
                question.getQuestionContext(),
                question.getImageUrl(),
                question.getQuestionType(),
                question.getDifficulty(),
                question.getSequenceNumber(),
                question.getMarks(),
                options
        );
    }
}