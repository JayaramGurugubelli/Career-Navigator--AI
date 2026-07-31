package career_Navigator_parent.savedjob.repository;

import career_Navigator_parent.savedjob.entity.SavedJob;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

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

    long countByStudentId(
            Long studentId
    );

    @Query(
            value = """
                    SELECT savedJob
                    FROM SavedJob savedJob
                    JOIN FETCH savedJob.jobPosting job
                    JOIN FETCH job.company company
                    WHERE savedJob.student.id = :studentId
                      AND (
                            :keyword IS NULL
                            OR LOWER(job.title)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(company.name)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                          )
                      AND (
                            :location IS NULL
                            OR LOWER(job.location)
                                LIKE LOWER(CONCAT('%', :location, '%'))
                          )
                    """,
            countQuery = """
                    SELECT COUNT(savedJob)
                    FROM SavedJob savedJob
                    JOIN savedJob.jobPosting job
                    JOIN job.company company
                    WHERE savedJob.student.id = :studentId
                      AND (
                            :keyword IS NULL
                            OR LOWER(job.title)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(company.name)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                          )
                      AND (
                            :location IS NULL
                            OR LOWER(job.location)
                                LIKE LOWER(CONCAT('%', :location, '%'))
                          )
                    """
    )
    Page<SavedJob> searchStudentSavedJobs(
            @Param("studentId")
            Long studentId,

            @Param("keyword")
            String keyword,

            @Param("location")
            String location,

            Pageable pageable
    );
}