package careerpilot_parent.job.repository;

import careerpilot_parent.company.enums.JobStatus;
import careerpilot_parent.job.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface JobPostingRepository
        extends JpaRepository<JobPosting, Long>,
        JpaSpecificationExecutor<JobPosting> {

    Optional<JobPosting> findBySlug(
            String slug
    );

    Optional<JobPosting> findBySlugAndStatus(
            String slug,
            JobStatus status
    );

    boolean existsBySlug(
            String slug
    );

    Page<JobPosting> findByRecruiterId(
            Long recruiterId,
            Pageable pageable
    );

    Page<JobPosting> findByRecruiterIdAndStatus(
            Long recruiterId,
            JobStatus status,
            Pageable pageable
    );

    Optional<JobPosting> findByIdAndRecruiterId(
            Long jobId,
            Long recruiterId
    );

    Page<JobPosting> findByCompanyId(
            Long companyId,
            Pageable pageable
    );

    Page<JobPosting> findByStatus(
            JobStatus status,
            Pageable pageable
    );

    long countByStatus(
            JobStatus status
    );

    /*
     * A published job that has not been closed
     * is treated as active.
     */
    long countByPublishedAtIsNotNullAndClosedAtIsNull();
}