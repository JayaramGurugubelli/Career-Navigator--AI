package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.AssessmentQuestion;
import career_Navigator_parent.learning.enums.QuestionDifficulty;
import career_Navigator_parent.learning.enums.QuestionType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentQuestionRepository
        extends JpaRepository<AssessmentQuestion, Long> {

    @EntityGraph(attributePaths = {
            "options"
    })
    List<AssessmentQuestion>
    findByAssessmentIdAndActiveTrueOrderBySequenceNumberAsc(
            Long assessmentId
    );

    @EntityGraph(attributePaths = {
            "options"
    })
    Optional<AssessmentQuestion>
    findByIdAndAssessmentId(
            Long questionId,
            Long assessmentId
    );

    boolean existsByAssessmentIdAndSequenceNumber(
            Long assessmentId,
            Integer sequenceNumber
    );

    long countByAssessmentIdAndActiveTrue(
            Long assessmentId
    );

    long countByAssessmentIdAndQuestionTypeAndActiveTrue(
            Long assessmentId,
            QuestionType questionType
    );

    long countByAssessmentIdAndDifficultyAndActiveTrue(
            Long assessmentId,
            QuestionDifficulty difficulty
    );
}