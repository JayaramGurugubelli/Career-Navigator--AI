package career_Navigator_parent.analytics.repository;

import career_Navigator_parent.analytics.projection.JobPerformanceProjection;
import career_Navigator_parent.analytics.projection.SourcePerformanceProjection;
import career_Navigator_parent.analytics.projection.StatusCountProjection;
import career_Navigator_parent.job.entity.JobPosting;
import career_Navigator_parent.interview.enums.InterviewStatus;
import career_Navigator_parent.offer.enums.OfferStatus;

import career_Navigator_parent.shared.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AnalyticsRepository
        extends JpaRepository<JobPosting, Long> {

    /*
     * ---------------------------------------------------------
     * JOB COUNTS
     * ---------------------------------------------------------
     */

    @Query("""
            SELECT COUNT(j.id)
            FROM JobPosting j
            WHERE j.recruiter.id = :recruiterId
              AND (:fromDate IS NULL OR j.createdAt >= :fromDate)
              AND (:toDate IS NULL OR j.createdAt < :toDate)
            """)
    long countJobs(
            @Param("recruiterId") Long recruiterId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("""
            SELECT COUNT(j.id)
            FROM JobPosting j
            WHERE j.recruiter.id = :recruiterId
              AND j.publishedAt IS NOT NULL
              AND j.closedAt IS NULL
              AND (:fromDate IS NULL OR j.createdAt >= :fromDate)
              AND (:toDate IS NULL OR j.createdAt < :toDate)
            """)
    long countActiveJobs(
            @Param("recruiterId") Long recruiterId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("""
            SELECT COUNT(j.id)
            FROM JobPosting j
            WHERE j.recruiter.id = :recruiterId
              AND j.closedAt IS NOT NULL
              AND (:fromDate IS NULL OR j.createdAt >= :fromDate)
              AND (:toDate IS NULL OR j.createdAt < :toDate)
            """)
    long countClosedJobs(
            @Param("recruiterId") Long recruiterId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("""
            SELECT COUNT(j.id)
            FROM JobPosting j
            WHERE j.recruiter.id = :recruiterId
              AND j.publishedAt IS NULL
              AND j.closedAt IS NULL
              AND (:fromDate IS NULL OR j.createdAt >= :fromDate)
              AND (:toDate IS NULL OR j.createdAt < :toDate)
            """)
    long countDraftJobs(
            @Param("recruiterId") Long recruiterId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    /*
     * ---------------------------------------------------------
     * APPLICATION COUNTS
     * ---------------------------------------------------------
     */

    @Query("""
            SELECT COUNT(a.id)
            FROM JobApplication a
            WHERE a.jobPosting.recruiter.id = :recruiterId
              AND (:fromDate IS NULL OR a.appliedAt >= :fromDate)
              AND (:toDate IS NULL OR a.appliedAt < :toDate)
            """)
    long countApplications(
            @Param("recruiterId") Long recruiterId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("""
            SELECT a.status AS status,
                   COUNT(a.id) AS total
            FROM JobApplication a
            WHERE a.jobPosting.recruiter.id = :recruiterId
              AND (:jobId IS NULL OR a.jobPosting.id = :jobId)
              AND (:fromDate IS NULL OR a.appliedAt >= :fromDate)
              AND (:toDate IS NULL OR a.appliedAt < :toDate)
            GROUP BY a.status
            """)
    List<StatusCountProjection> countApplicationsByStatus(
            @Param("recruiterId") Long recruiterId,
            @Param("jobId") Long jobId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("""
            SELECT COUNT(a.id)
            FROM JobApplication a
            WHERE a.jobPosting.recruiter.id = :recruiterId
              AND a.status = :status
              AND (:jobId IS NULL OR a.jobPosting.id = :jobId)
              AND (:fromDate IS NULL OR a.appliedAt >= :fromDate)
              AND (:toDate IS NULL OR a.appliedAt < :toDate)
            """)
    long countApplicationsByStatus(
            @Param("recruiterId") Long recruiterId,
            @Param("jobId") Long jobId,
            @Param("status") ApplicationStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    /*
     * ---------------------------------------------------------
     * INTERVIEW COUNTS
     * ---------------------------------------------------------
     */

    @Query("""
            SELECT COUNT(i.id)
            FROM Interview i
            WHERE i.jobApplication.jobPosting.recruiter.id = :recruiterId
              AND (:jobId IS NULL
                   OR i.jobApplication.jobPosting.id = :jobId)
              AND (:fromDate IS NULL OR i.createdAt >= :fromDate)
              AND (:toDate IS NULL OR i.createdAt < :toDate)
            """)
    long countInterviews(
            @Param("recruiterId") Long recruiterId,
            @Param("jobId") Long jobId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("""
            SELECT COUNT(i.id)
            FROM Interview i
            WHERE i.jobApplication.jobPosting.recruiter.id = :recruiterId
              AND i.status = :status
              AND (:jobId IS NULL
                   OR i.jobApplication.jobPosting.id = :jobId)
              AND (:fromDate IS NULL OR i.createdAt >= :fromDate)
              AND (:toDate IS NULL OR i.createdAt < :toDate)
            """)
    long countInterviewsByStatus(
            @Param("recruiterId") Long recruiterId,
            @Param("jobId") Long jobId,
            @Param("status") InterviewStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    /*
     * ---------------------------------------------------------
     * OFFER COUNTS
     * ---------------------------------------------------------
     */

    @Query("""
            SELECT COUNT(o.id)
            FROM JobOffer o
            WHERE o.jobApplication.jobPosting.recruiter.id = :recruiterId
              AND (:jobId IS NULL
                   OR o.jobApplication.jobPosting.id = :jobId)
              AND (:fromDate IS NULL OR o.createdAt >= :fromDate)
              AND (:toDate IS NULL OR o.createdAt < :toDate)
            """)
    long countOffers(
            @Param("recruiterId") Long recruiterId,
            @Param("jobId") Long jobId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("""
            SELECT COUNT(o.id)
            FROM JobOffer o
            WHERE o.jobApplication.jobPosting.recruiter.id = :recruiterId
              AND o.status = :status
              AND (:jobId IS NULL
                   OR o.jobApplication.jobPosting.id = :jobId)
              AND (:fromDate IS NULL OR o.createdAt >= :fromDate)
              AND (:toDate IS NULL OR o.createdAt < :toDate)
            """)
    long countOffersByStatus(
            @Param("recruiterId") Long recruiterId,
            @Param("jobId") Long jobId,
            @Param("status") OfferStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    /*
     * ---------------------------------------------------------
     * JOB ANALYTICS
     * ---------------------------------------------------------
     */

    @Query(
            value = """
                    SELECT
                        j.id AS jobId,
                        j.title AS jobTitle,
                        CAST(j.status AS string) AS status,

                        (
                            SELECT COUNT(a.id)
                            FROM JobApplication a
                            WHERE a.jobPosting.id = j.id
                        ) AS totalApplications,

                        (
                            SELECT COUNT(i.id)
                            FROM Interview i
                            WHERE i.jobApplication.jobPosting.id = j.id
                        ) AS interviewCount,

                        (
                            SELECT COUNT(o.id)
                            FROM JobOffer o
                            WHERE o.jobApplication.jobPosting.id = j.id
                        ) AS offerCount,

                        (
                            SELECT COUNT(o2.id)
                            FROM JobOffer o2
                            WHERE o2.jobApplication.jobPosting.id = j.id
                              AND o2.status = :acceptedOfferStatus
                        ) AS hireCount,

                        j.publishedAt AS publishedAt,
                        j.closedAt AS closedAt

                    FROM JobPosting j
                    WHERE j.recruiter.id = :recruiterId
                      AND (
                            :keyword IS NULL
                            OR LOWER(j.title)
                               LIKE LOWER(CONCAT('%', :keyword, '%'))
                      )
                      AND (:fromDate IS NULL OR j.createdAt >= :fromDate)
                      AND (:toDate IS NULL OR j.createdAt < :toDate)
                    """,
            countQuery = """
                    SELECT COUNT(j.id)
                    FROM JobPosting j
                    WHERE j.recruiter.id = :recruiterId
                      AND (
                            :keyword IS NULL
                            OR LOWER(j.title)
                               LIKE LOWER(CONCAT('%', :keyword, '%'))
                      )
                      AND (:fromDate IS NULL OR j.createdAt >= :fromDate)
                      AND (:toDate IS NULL OR j.createdAt < :toDate)
                    """
    )
    Page<JobPerformanceProjection> findJobPerformance(
            @Param("recruiterId") Long recruiterId,
            @Param("keyword") String keyword,
            @Param("acceptedOfferStatus")
            OfferStatus acceptedOfferStatus,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query("""
            SELECT
                j.id AS jobId,
                j.title AS jobTitle,
                CAST(j.status AS string) AS status,

                (
                    SELECT COUNT(a.id)
                    FROM JobApplication a
                    WHERE a.jobPosting.id = j.id
                ) AS totalApplications,

                (
                    SELECT COUNT(i.id)
                    FROM Interview i
                    WHERE i.jobApplication.jobPosting.id = j.id
                ) AS interviewCount,

                (
                    SELECT COUNT(o.id)
                    FROM JobOffer o
                    WHERE o.jobApplication.jobPosting.id = j.id
                ) AS offerCount,

                (
                    SELECT COUNT(o2.id)
                    FROM JobOffer o2
                    WHERE o2.jobApplication.jobPosting.id = j.id
                      AND o2.status = :acceptedOfferStatus
                ) AS hireCount,

                j.publishedAt AS publishedAt,
                j.closedAt AS closedAt

            FROM JobPosting j
            WHERE j.id = :jobId
              AND j.recruiter.id = :recruiterId
            """)
    Optional<JobPerformanceProjection> findJobPerformanceById(
            @Param("recruiterId") Long recruiterId,
            @Param("jobId") Long jobId,
            @Param("acceptedOfferStatus")
            OfferStatus acceptedOfferStatus
    );

    /*
     * ---------------------------------------------------------
     * SOURCE ANALYTICS
     * ---------------------------------------------------------
     *
     * Use only if JobApplication contains applicationSource.
     */

    @Query("""
            SELECT
                COALESCE(a.applicationSource, 'DIRECT') AS source,

                COUNT(a.id) AS applicationCount,

                (
                    SELECT COUNT(i.id)
                    FROM Interview i
                    WHERE i.jobApplication.jobPosting.recruiter.id
                          = :recruiterId
                      AND COALESCE(
                              i.jobApplication.applicationSource,
                              'DIRECT'
                          ) = COALESCE(a.applicationSource, 'DIRECT')
                ) AS interviewCount,

                (
                    SELECT COUNT(o.id)
                    FROM JobOffer o
                    WHERE o.jobApplication.jobPosting.recruiter.id
                          = :recruiterId
                      AND COALESCE(
                              o.jobApplication.applicationSource,
                              'DIRECT'
                          ) = COALESCE(a.applicationSource, 'DIRECT')
                ) AS offerCount,

                (
                    SELECT COUNT(o2.id)
                    FROM JobOffer o2
                    WHERE o2.jobApplication.jobPosting.recruiter.id
                          = :recruiterId
                      AND o2.status = :acceptedOfferStatus
                      AND COALESCE(
                              o2.jobApplication.applicationSource,
                              'DIRECT'
                          ) = COALESCE(a.applicationSource, 'DIRECT')
                ) AS hireCount

            FROM JobApplication a
            WHERE a.jobPosting.recruiter.id = :recruiterId
              AND (:fromDate IS NULL OR a.appliedAt >= :fromDate)
              AND (:toDate IS NULL OR a.appliedAt < :toDate)
            GROUP BY a.applicationSource
            ORDER BY COUNT(a.id) DESC
            """)
    List<SourcePerformanceProjection> findSourcePerformance(
            @Param("recruiterId") Long recruiterId,
            @Param("acceptedOfferStatus")
            OfferStatus acceptedOfferStatus,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}