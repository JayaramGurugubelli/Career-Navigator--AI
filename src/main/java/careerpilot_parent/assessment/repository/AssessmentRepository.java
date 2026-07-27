package careerpilot_parent.assessment.repository;

import careerpilot_parent.assessment.entity.Assessment;
import careerpilot_parent.assessment.enums.AssessmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface AssessmentRepository
        extends JpaRepository<Assessment, Long> {

    Page<Assessment> findByRecruiterId(
            Long recruiterId,
            Pageable pageable
    );

    Page<Assessment> findByRecruiterIdAndStatus(
            Long recruiterId,
            AssessmentStatus status,
            Pageable pageable
    );

    Page<Assessment> findByJobApplicationId(
            Long applicationId,
            Pageable pageable
    );

    Page<Assessment> findByJobApplicationStudentId(
            Long studentId,
            Pageable pageable
    );

    Page<Assessment> findByJobApplicationStudentIdAndStatus(
            Long studentId,
            AssessmentStatus status,
            Pageable pageable
    );

    Optional<Assessment> findByIdAndRecruiterId(
            Long assessmentId,
            Long recruiterId
    );

    Optional<Assessment> findByIdAndJobApplicationStudentId(
            Long assessmentId,
            Long studentId
    );

    boolean existsByJobApplicationIdAndStatusIn(
            Long applicationId,
            Collection<AssessmentStatus> statuses
    );
}