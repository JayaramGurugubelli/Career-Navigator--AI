package career_Navigator_parent.jobrecommendation.repository;

import career_Navigator_parent.company.enums.JobStatus;
import career_Navigator_parent.job.entity.JobPosting;
import career_Navigator_parent.jobrecommendation.entity.JobRecommendation;
import career_Navigator_parent.jobrecommendation.enums.RecommendationSource;
import career_Navigator_parent.student.entity.Student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JobRecommendationRepository
        extends JpaRepository<JobRecommendation, Long> {

    Optional<JobRecommendation>
    findByStudentIdAndJobPostingId(
            Long studentId,
            Long jobPostingId
    );

    Optional<JobRecommendation>
    findByStudentIdAndJobPostingIdAndActiveTrue(
            Long studentId,
            Long jobPostingId
    );

    @Query(
            value = """
                    SELECT recommendation
                    FROM JobRecommendation recommendation
                    JOIN FETCH recommendation.jobPosting job
                    JOIN FETCH job.company company
                    WHERE recommendation.student.id = :studentId
                      AND recommendation.active = true
                      AND recommendation.expiresAt > :now
                      AND recommendation.matchScore >= :minimumScore
                      AND (
                            :source IS NULL
                            OR recommendation.source = :source
                          )
                      AND job.status = :jobStatus
                      AND (
                            job.applicationDeadline IS NULL
                            OR job.applicationDeadline >= :today
                          )
                    """,
            countQuery = """
                    SELECT COUNT(recommendation)
                    FROM JobRecommendation recommendation
                    JOIN recommendation.jobPosting job
                    WHERE recommendation.student.id = :studentId
                      AND recommendation.active = true
                      AND recommendation.expiresAt > :now
                      AND recommendation.matchScore >= :minimumScore
                      AND (
                            :source IS NULL
                            OR recommendation.source = :source
                          )
                      AND job.status = :jobStatus
                      AND (
                            job.applicationDeadline IS NULL
                            OR job.applicationDeadline >= :today
                          )
                    """
    )
    Page<JobRecommendation> findStudentRecommendations(
            @Param("studentId")
            Long studentId,

            @Param("minimumScore")
            Double minimumScore,

            @Param("source")
            RecommendationSource source,

            @Param("jobStatus")
            JobStatus jobStatus,

            @Param("today")
            LocalDate today,

            @Param("now")
            LocalDateTime now,

            Pageable pageable
    );

    @Query("""
            SELECT recommendation
            FROM JobRecommendation recommendation
            JOIN FETCH recommendation.jobPosting job
            JOIN FETCH job.company company
            WHERE recommendation.student.id = :studentId
              AND job.id = :jobId
              AND recommendation.active = true
              AND recommendation.expiresAt > :now
            """)
    Optional<JobRecommendation> findActiveStudentRecommendation(
            @Param("studentId")
            Long studentId,

            @Param("jobId")
            Long jobId,

            @Param("now")
            LocalDateTime now
    );

    @Query(
            value = """
                    SELECT job
                    FROM JobPosting job
                    JOIN FETCH job.company company
                    WHERE job.status = :jobStatus
                      AND (
                            job.applicationDeadline IS NULL
                            OR job.applicationDeadline >= :today
                          )
                      AND NOT EXISTS (
                            SELECT application.id
                            FROM JobApplication application
                            WHERE application.student.id = :studentId
                              AND application.jobPosting.id = job.id
                          )
                    ORDER BY job.publishedAt DESC
                    """,
            countQuery = """
                    SELECT COUNT(job)
                    FROM JobPosting job
                    WHERE job.status = :jobStatus
                      AND (
                            job.applicationDeadline IS NULL
                            OR job.applicationDeadline >= :today
                          )
                      AND NOT EXISTS (
                            SELECT application.id
                            FROM JobApplication application
                            WHERE application.student.id = :studentId
                              AND application.jobPosting.id = job.id
                          )
                    """
    )
    Page<JobPosting> findRecommendationCandidates(
            @Param("studentId")
            Long studentId,

            @Param("jobStatus")
            JobStatus jobStatus,

            @Param("today")
            LocalDate today,

            Pageable pageable
    );

    @Query("""
            SELECT job
            FROM SavedJob savedJob
            JOIN savedJob.jobPosting job
            WHERE savedJob.student.id = :studentId
            ORDER BY savedJob.savedAt DESC
            """)
    List<JobPosting> findRecentlySavedJobs(
            @Param("studentId")
            Long studentId,

            Pageable pageable
    );

    /*
     * Loads experiences while the student is attached to the
     * current Hibernate session.
     */
    @Query("""
            SELECT DISTINCT student
            FROM Student student
            LEFT JOIN FETCH student.experiences
            WHERE student.id = :studentId
            """)
    Optional<Student> findStudentWithExperiences(
            @Param("studentId")
            Long studentId
    );

    @Query(
            value = """
                    SELECT skill_name
                    FROM student_skills
                    WHERE student_id = :studentId
                    """,
            nativeQuery = true
    )
    List<String> findStudentSkillNames(
            @Param("studentId")
            Long studentId
    );

    @Query("""
            SELECT savedJob.jobPosting.id
            FROM SavedJob savedJob
            WHERE savedJob.student.id = :studentId
              AND savedJob.jobPosting.id IN :jobIds
            """)
    List<Long> findSavedJobIds(
            @Param("studentId")
            Long studentId,

            @Param("jobIds")
            List<Long> jobIds
    );

    @Query("""
            SELECT application.jobPosting.id
            FROM JobApplication application
            WHERE application.student.id = :studentId
              AND application.jobPosting.id IN :jobIds
            """)
    List<Long> findAppliedJobIds(
            @Param("studentId")
            Long studentId,

            @Param("jobIds")
            List<Long> jobIds
    );

    /*
     * Do not use clearAutomatically=true here.
     *
     * Clearing the persistence context detaches Student and
     * causes lazy-loading failures for experiences.
     */
    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE JobRecommendation recommendation
            SET recommendation.active = false
            WHERE recommendation.student.id = :studentId
              AND recommendation.active = true
            """)
    int deactivateStudentRecommendations(
            @Param("studentId")
            Long studentId
    );

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE JobRecommendation recommendation
            SET recommendation.active = false
            WHERE recommendation.expiresAt <= :now
              AND recommendation.active = true
            """)
    int deactivateExpiredRecommendations(
            @Param("now")
            LocalDateTime now
    );

    @Query("""
            SELECT student.id
            FROM Student student
            WHERE student.activelyLooking = true
            ORDER BY student.id
            """)
    Page<Long> findActiveStudentIds(
            Pageable pageable
    );
}