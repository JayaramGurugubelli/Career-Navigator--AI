package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.Assessment;
import career_Navigator_parent.learning.enums.AssessmentStatus;
import career_Navigator_parent.learning.enums.AssessmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssessmentLearningRepository
        extends JpaRepository<Assessment, Long> {

    List<Assessment> findByCourseIdAndStatusAndActiveTrue(
            Long courseId,
            AssessmentStatus status
    );

    List<Assessment> findByMilestoneIdAndStatusAndActiveTrue(
            Long milestoneId,
            AssessmentStatus status
    );

    Page<Assessment> findByAssessmentTypeAndStatusAndActiveTrue(
            AssessmentType assessmentType,
            AssessmentStatus status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "course",
            "milestone"
    })
    @Query("""
            select assessment
            from LearningAssessment assessment
            where assessment.id = :assessmentId
            """)
    Optional<Assessment> findDetailedById(
            @Param("assessmentId")
            Long assessmentId
    );

    @EntityGraph(attributePaths = {
            "course",
            "milestone"
    })
    @Query("""
            select assessment
            from LearningAssessment assessment
            where assessment.id = :assessmentId
              and assessment.status = :status
              and assessment.active = true
            """)
    Optional<Assessment> findPublishedDetailedById(
            @Param("assessmentId")
            Long assessmentId,

            @Param("status")
            AssessmentStatus status
    );

    long countByCourseIdAndStatusAndActiveTrue(
            Long courseId,
            AssessmentStatus status
    );
}