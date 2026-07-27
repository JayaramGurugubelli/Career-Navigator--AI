package careerpilot_parent.job.repository;

import careerpilot_parent.job.entity.JobApplication;
import careerpilot_parent.company.enums.ApplicationStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    boolean existsByStudentIdAndJobPostingId(
            Long studentId,
            Long jobPostingId
    );

    Optional<JobApplication> findByIdAndStudentId(
            Long applicationId,
            Long studentId
    );

    Page<JobApplication> findByStudentId(
            Long studentId,
            Pageable pageable
    );

    Page<JobApplication> findByStudentIdAndStatus(
            Long studentId,
            ApplicationStatus status,
            Pageable pageable
    );

    Optional<JobApplication>
    findByIdAndJobPostingRecruiterId(
            Long applicationId,
            Long recruiterId
    );

    Page<JobApplication>
    findByJobPostingIdAndJobPostingRecruiterId(
            Long jobPostingId,
            Long recruiterId,
            Pageable pageable
    );

    Page<JobApplication>
    findByJobPostingIdAndJobPostingRecruiterIdAndStatus(
            Long jobPostingId,
            Long recruiterId,
            ApplicationStatus status,
            Pageable pageable
    );
    Page<JobApplication> findByJobPostingId(
            Long jobPostingId,
            Pageable pageable
    );

    Page<JobApplication> findByJobPostingIdAndStatus(
            Long jobPostingId,
            ApplicationStatus status,
            Pageable pageable
    );


}