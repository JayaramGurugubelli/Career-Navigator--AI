package career_Navigator_parent.interview.repository;

import career_Navigator_parent.interview.entity.Interview;
import career_Navigator_parent.interview.enums.InterviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    Page<Interview> findByRecruiterId(Long recruiterId, Pageable pageable);

    Page<Interview> findByRecruiterIdAndStatus(Long recruiterId, InterviewStatus status, Pageable pageable);

    Page<Interview> findByJobApplicationId(
            Long applicationId,
            Pageable pageable
    );

    Page<Interview> findByJobApplicationIdAndRecruiterId(
            Long applicationId,
            Long recruiterId,
            Pageable pageable
    );

    Optional<Interview> findByIdAndRecruiterId(
            Long interviewId,
            Long recruiterId
    );

    Page<Interview> findByJobApplicationStudentId(
            Long studentId,
            Pageable pageable
    );

    Page<Interview> findByJobApplicationStudentIdAndStatus(
            Long studentId,
            InterviewStatus status,
            Pageable pageable
    );

    Optional<Interview> findByIdAndJobApplicationStudentId(
            Long interviewId,
            Long studentId
    );

    boolean existsByJobApplicationIdAndStatusIn(
            Long applicationId,
            Collection<InterviewStatus> statuses
    );

    long countByJobApplicationId(
            Long applicationId
    );
    long countByStatus(InterviewStatus status);
}