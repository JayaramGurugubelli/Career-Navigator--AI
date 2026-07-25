package careerpilot_parent.job.repository;

import careerpilot_parent.job.entity.SavedJob;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavedJobRepository
        extends JpaRepository<SavedJob, Long> {

    boolean existsByStudentIdAndJobPostingId(
            Long studentId,
            Long jobPostingId
    );

    Optional<SavedJob> findByStudentIdAndJobPostingId(
            Long studentId,
            Long jobPostingId
    );

    Page<SavedJob> findByStudentId(
            Long studentId,
            Pageable pageable
    );
}