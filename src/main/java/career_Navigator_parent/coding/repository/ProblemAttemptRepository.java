package career_Navigator_parent.coding.repository;

import career_Navigator_parent.coding.entity.ProblemAttempt;
import career_Navigator_parent.coding.enums.ProblemAttemptStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProblemAttemptRepository
        extends JpaRepository<ProblemAttempt, Long> {

    Optional<ProblemAttempt> findByStudentIdAndProblemId(
            Long studentId,
            Long problemId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select attempt
            from ProblemAttempt attempt
            where attempt.student.id = :studentId
              and attempt.problem.id = :problemId
            """)
    Optional<ProblemAttempt> findForUpdate(
            @Param("studentId")
            Long studentId,

            @Param("problemId")
            Long problemId
    );

    boolean existsByStudentIdAndProblemIdAndStatus(
            Long studentId,
            Long problemId,
            ProblemAttemptStatus status
    );

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(
            Long studentId,
            ProblemAttemptStatus status
    );

    Page<ProblemAttempt> findByStudentId(
            Long studentId,
            Pageable pageable
    );

    Page<ProblemAttempt> findByStudentIdAndStatus(
            Long studentId,
            ProblemAttemptStatus status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "problem",
            "problem.tags"
    })
    List<ProblemAttempt>
    findTop10ByStudentIdOrderByLastAttemptedAtDesc(
            Long studentId
    );

    @EntityGraph(attributePaths = {
            "problem",
            "problem.tags"
    })
    @Query("""
            select distinct attempt
            from ProblemAttempt attempt
            where attempt.student.id = :studentId
              and (
                    :status is null
                    or attempt.status = :status
              )
            """)
    Page<ProblemAttempt> searchStudentAttempts(
            @Param("studentId")
            Long studentId,

            @Param("status")
            ProblemAttemptStatus status,

            Pageable pageable
    );

    @Query("""
            select max(attempt.lastAttemptedAt)
            from ProblemAttempt attempt
            where attempt.student.id = :studentId
            """)
    LocalDateTime findLastActivityAt(
            @Param("studentId")
            Long studentId
    );

    @EntityGraph(attributePaths = {
            "problem",
            "problem.tags"
    })
    @Query("""
            select distinct attempt
            from ProblemAttempt attempt
            where attempt.student.id = :studentId
            """)
    List<ProblemAttempt> findAllWithProblemAndTags(
            @Param("studentId")
            Long studentId
    );

    @EntityGraph(attributePaths = {
            "problem"
    })
    @Query("""
            select attempt
            from ProblemAttempt attempt
            where attempt.student.id = :studentId
              and attempt.lastAttemptedAt >= :fromDate
              and attempt.lastAttemptedAt < :toDate
            order by attempt.lastAttemptedAt desc
            """)
    List<ProblemAttempt> findStudentActivityBetween(
            @Param("studentId")
            Long studentId,

            @Param("fromDate")
            LocalDateTime fromDate,

            @Param("toDate")
            LocalDateTime toDate
    );
}