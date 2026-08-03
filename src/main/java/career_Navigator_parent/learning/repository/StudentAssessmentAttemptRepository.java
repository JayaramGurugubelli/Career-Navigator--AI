package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.StudentAssessmentAttempt;
import career_Navigator_parent.learning.enums.AssessmentAttemptStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentAssessmentAttemptRepository
        extends JpaRepository<StudentAssessmentAttempt, Long> {

    @EntityGraph(attributePaths = {
            "assessment",
            "answers",
            "answers.question",
            "answers.selectedOptions"
    })
    Optional<StudentAssessmentAttempt>
    findByIdAndStudentId(
            Long attemptId,
            Long studentId
    );

    Page<StudentAssessmentAttempt>
    findByStudentIdOrderByStartedAtDesc(
            Long studentId,
            Pageable pageable
    );

    long countByStudentIdAndAssessmentId(
            Long studentId,
            Long assessmentId
    );

    boolean existsByStudentIdAndAssessmentIdAndStatusIn(
            Long studentId,
            Long assessmentId,
            Iterable<AssessmentAttemptStatus> statuses
    );

    Optional<StudentAssessmentAttempt>
    findTopByStudentIdAndAssessmentIdOrderByAttemptNumberDesc(
            Long studentId,
            Long assessmentId
    );
}