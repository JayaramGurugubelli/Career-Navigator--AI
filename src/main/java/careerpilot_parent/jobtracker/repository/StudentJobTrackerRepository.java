package careerpilot_parent.jobtracker.repository;

import careerpilot_parent.job.entity.JobApplication;
import careerpilot_parent.shared.enums.ApplicationStatus;
import careerpilot_parent.interview.entity.Interview;
import careerpilot_parent.interview.enums.InterviewStatus;
import careerpilot_parent.jobtracker.projection.ApplicationStatusCountProjection;
import careerpilot_parent.jobtracker.projection.StudentApplicationTrackerProjection;
import careerpilot_parent.offer.entity.JobOffer;
import careerpilot_parent.offer.enums.OfferStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StudentJobTrackerRepository
        extends JpaRepository<JobApplication, Long> {

    /*
     * ---------------------------------------------------------
     * APPLICATION OWNERSHIP
     * ---------------------------------------------------------
     */

    @Query("""
            SELECT a
            FROM JobApplication a
            JOIN FETCH a.jobPosting j
            JOIN FETCH j.company c
            WHERE a.id = :applicationId
              AND a.student.id = :studentId
            """)
    Optional<JobApplication> findApplicationForStudent(
            @Param("applicationId") Long applicationId,
            @Param("studentId") Long studentId
    );

    /*
     * ---------------------------------------------------------
     * DASHBOARD
     * ---------------------------------------------------------
     */

    @Query("""
            SELECT a.status AS status,
                   COUNT(a.id) AS total
            FROM JobApplication a
            WHERE a.student.id = :studentId
              AND (:fromDate IS NULL OR a.appliedAt >= :fromDate)
              AND (:toDate IS NULL OR a.appliedAt < :toDate)
            GROUP BY a.status
            """)
    List<ApplicationStatusCountProjection>
    countApplicationsByStatus(
            @Param("studentId") Long studentId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("""
            SELECT COUNT(a.id)
            FROM JobApplication a
            WHERE a.student.id = :studentId
              AND (:fromDate IS NULL OR a.appliedAt >= :fromDate)
              AND (:toDate IS NULL OR a.appliedAt < :toDate)
            """)
    long countApplications(
            @Param("studentId") Long studentId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("""
            SELECT COUNT(i.id)
            FROM Interview i
            WHERE i.jobApplication.student.id = :studentId
              AND i.scheduledAt >= :now
              AND i.status IN :statuses
            """)
    long countUpcomingInterviews(
            @Param("studentId") Long studentId,
            @Param("now") LocalDateTime now,
            @Param("statuses")
            List<InterviewStatus> statuses
    );

    @Query("""
            SELECT COUNT(o.id)
            FROM JobOffer o
            WHERE o.jobApplication.student.id = :studentId
              AND o.status IN :statuses
            """)
    long countPendingOffers(
            @Param("studentId") Long studentId,
            @Param("statuses") List<OfferStatus> statuses
    );

    /*
     * ---------------------------------------------------------
     * APPLICATION TRACKER LIST
     * ---------------------------------------------------------
     */

    @Query(
            value = """
                    SELECT
                        a.id AS applicationId,

                        j.id AS jobId,
                        j.title AS jobTitle,

                        c.id AS companyId,
                        c.name AS companyName,
                        c.logoUrl AS companyLogoUrl,

                        j.location AS location,

                        CAST(j.employmentType AS string)
                            AS employmentType,

                        CAST(j.workMode AS string)
                            AS workMode,

                        CAST(a.status AS string)
                            AS applicationStatus,

                        a.appliedAt AS appliedAt,
                        a.updatedAt AS lastUpdatedAt,

                        (
                            SELECT MIN(i.scheduledAt)
                            FROM Interview i
                            WHERE i.jobApplication.id = a.id
                              AND i.scheduledAt >= CURRENT_TIMESTAMP
                              AND i.status IN :activeInterviewStatuses
                        ) AS nextInterviewAt,

                        (
                            SELECT CAST(i2.interviewType AS string)
                            FROM Interview i2
                            WHERE i2.jobApplication.id = a.id
                              AND i2.scheduledAt = (
                                  SELECT MIN(i3.scheduledAt)
                                  FROM Interview i3
                                  WHERE i3.jobApplication.id = a.id
                                    AND i3.scheduledAt >= CURRENT_TIMESTAMP
                                    AND i3.status IN :activeInterviewStatuses
                              )
                        ) AS nextInterviewType,

                        (
                            SELECT CAST(i4.interviewMode AS string)
                            FROM Interview i4
                            WHERE i4.jobApplication.id = a.id
                              AND i4.scheduledAt = (
                                  SELECT MIN(i5.scheduledAt)
                                  FROM Interview i5
                                  WHERE i5.jobApplication.id = a.id
                                    AND i5.scheduledAt >= CURRENT_TIMESTAMP
                                    AND i5.status IN :activeInterviewStatuses
                              )
                        ) AS nextInterviewMode,

                        (
                            SELECT MAX(o.id)
                            FROM JobOffer o
                            WHERE o.jobApplication.id = a.id
                        ) AS offerId,

                        (
                            SELECT CAST(o2.status AS string)
                            FROM JobOffer o2
                            WHERE o2.id = (
                                SELECT MAX(o3.id)
                                FROM JobOffer o3
                                WHERE o3.jobApplication.id = a.id
                            )
                        ) AS offerStatus

                    FROM JobApplication a
                    JOIN a.jobPosting j
                    JOIN j.company c

                    WHERE a.student.id = :studentId

                      AND (
                            :status IS NULL
                            OR a.status = :status
                      )

                      AND (
                            :keyword IS NULL
                            OR LOWER(j.title)
                               LIKE LOWER(
                                   CONCAT('%', :keyword, '%')
                               )
                            OR LOWER(c.name)
                               LIKE LOWER(
                                   CONCAT('%', :keyword, '%')
                               )
                      )

                      AND (
                            :fromDate IS NULL
                            OR a.appliedAt >= :fromDate
                      )

                      AND (
                            :toDate IS NULL
                            OR a.appliedAt < :toDate
                      )
                    """,

            countQuery = """
                    SELECT COUNT(a.id)
                    FROM JobApplication a
                    JOIN a.jobPosting j
                    JOIN j.company c

                    WHERE a.student.id = :studentId

                      AND (
                            :status IS NULL
                            OR a.status = :status
                      )

                      AND (
                            :keyword IS NULL
                            OR LOWER(j.title)
                               LIKE LOWER(
                                   CONCAT('%', :keyword, '%')
                               )
                            OR LOWER(c.name)
                               LIKE LOWER(
                                   CONCAT('%', :keyword, '%')
                               )
                      )

                      AND (
                            :fromDate IS NULL
                            OR a.appliedAt >= :fromDate
                      )

                      AND (
                            :toDate IS NULL
                            OR a.appliedAt < :toDate
                      )
                    """
    )
    Page<StudentApplicationTrackerProjection>
    findApplicationTrackerItems(
            @Param("studentId") Long studentId,

            @Param("status")
            ApplicationStatus status,

            @Param("keyword")
            String keyword,

            @Param("fromDate")
            LocalDateTime fromDate,

            @Param("toDate")
            LocalDateTime toDate,

            @Param("activeInterviewStatuses")
            List<InterviewStatus> activeInterviewStatuses,

            Pageable pageable
    );

    /*
     * ---------------------------------------------------------
     * INTERVIEWS
     * ---------------------------------------------------------
     */

    @Query("""
            SELECT i
            FROM Interview i
            JOIN FETCH i.jobApplication a
            JOIN FETCH a.jobPosting j
            JOIN FETCH j.company c
            WHERE a.student.id = :studentId
              AND i.scheduledAt >= :now
              AND i.status IN :statuses
            ORDER BY i.scheduledAt ASC
            """)
    List<Interview> findUpcomingInterviews(
            @Param("studentId") Long studentId,
            @Param("now") LocalDateTime now,
            @Param("statuses")
            List<InterviewStatus> statuses
    );

    @Query("""
            SELECT i
            FROM Interview i
            WHERE i.jobApplication.id = :applicationId
              AND i.jobApplication.student.id = :studentId
            ORDER BY i.scheduledAt ASC
            """)
    List<Interview> findApplicationInterviews(
            @Param("applicationId") Long applicationId,
            @Param("studentId") Long studentId
    );

    /*
     * ---------------------------------------------------------
     * OFFERS
     * ---------------------------------------------------------
     */

    @Query("""
            SELECT o
            FROM JobOffer o
            JOIN FETCH o.jobApplication a
            JOIN FETCH a.jobPosting j
            JOIN FETCH j.company c
            WHERE a.student.id = :studentId
              AND (:status IS NULL OR o.status = :status)
            ORDER BY o.createdAt DESC
            """)
    List<JobOffer> findStudentOffers(
            @Param("studentId") Long studentId,
            @Param("status") OfferStatus status
    );

    @Query("""
            SELECT o
            FROM JobOffer o
            WHERE o.jobApplication.id = :applicationId
              AND o.jobApplication.student.id = :studentId
            ORDER BY o.createdAt ASC
            """)
    List<JobOffer> findApplicationOffers(
            @Param("applicationId") Long applicationId,
            @Param("studentId") Long studentId
    );
}