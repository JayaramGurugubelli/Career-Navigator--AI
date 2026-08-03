package career_Navigator_parent.coding.repository;

import career_Navigator_parent.coding.entity.CodeSubmission;
import career_Navigator_parent.coding.enums.SubmissionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CodeSubmissionRepository
        extends JpaRepository<CodeSubmission, Long> {

    @EntityGraph(attributePaths = {
            "problem",
            "testCaseResults",
            "testCaseResults.testCase"
    })
    Optional<CodeSubmission> findByIdAndStudentId(
            Long submissionId,
            Long studentId
    );

    @EntityGraph(attributePaths = {
            "problem"
    })
    Page<CodeSubmission> findByStudentIdOrderBySubmittedAtDesc(
            Long studentId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "problem"
    })
    Page<CodeSubmission>
    findByStudentIdAndProblemIdOrderBySubmittedAtDesc(
            Long studentId,
            Long problemId,
            Pageable pageable
    );

    Page<CodeSubmission> findByStudentId(
            Long studentId,
            Pageable pageable
    );

    Page<CodeSubmission> findByStudentIdAndProblemId(
            Long studentId,
            Long problemId,
            Pageable pageable
    );

    long countByStudentId(
            Long studentId
    );

    long countByStudentIdAndStatus(
            Long studentId,
            SubmissionStatus status
    );

    /*
     * Do not fetch problem.testCases and testCaseResults together here.
     * Both collections remain accessible inside the transactional
     * judging method through lazy loading.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {
            "student",
            "problem"
    })
    @Query("""
            select submission
            from CodeSubmission submission
            where submission.id = :submissionId
            """)
    Optional<CodeSubmission> findForJudging(
            @Param("submissionId")
            Long submissionId
    );

    @Query("""
            select count(submission)
            from CodeSubmission submission
            where submission.student.id = :studentId
              and submission.submittedAt >= :fromDate
              and submission.submittedAt < :toDate
            """)
    long countStudentSubmissionsBetween(
            @Param("studentId")
            Long studentId,

            @Param("fromDate")
            LocalDateTime fromDate,

            @Param("toDate")
            LocalDateTime toDate
    );

    @Query("""
            select count(submission)
            from CodeSubmission submission
            where submission.student.id = :studentId
              and submission.status = :status
              and submission.submittedAt >= :fromDate
              and submission.submittedAt < :toDate
            """)
    long countStudentSubmissionsByStatusBetween(
            @Param("studentId")
            Long studentId,

            @Param("status")
            SubmissionStatus status,

            @Param("fromDate")
            LocalDateTime fromDate,

            @Param("toDate")
            LocalDateTime toDate
    );

    @Query("""
            select count(submission)
            from CodeSubmission submission
            where submission.student.id = :studentId
              and submission.problem.id = :problemId
            """)
    long countByStudentAndProblem(
            @Param("studentId")
            Long studentId,

            @Param("problemId")
            Long problemId
    );

    @Query("""
            select count(submission)
            from CodeSubmission submission
            where submission.student.id = :studentId
              and submission.problem.id = :problemId
              and submission.status = :status
            """)
    long countByStudentAndProblemAndStatus(
            @Param("studentId")
            Long studentId,

            @Param("problemId")
            Long problemId,

            @Param("status")
            SubmissionStatus status
    );
}