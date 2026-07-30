package careerpilot_parent.coding.repository;

import careerpilot_parent.coding.entity.ProblemAttempt;
import careerpilot_parent.coding.enums.ProblemAttemptStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
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

    @Query("""
            select attempt
            from ProblemAttempt attempt
            join fetch attempt.problem problem
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

    @Query("""
            select distinct attempt
            from ProblemAttempt attempt
            join fetch attempt.problem problem
            left join fetch problem.tags tags
            where attempt.student.id = :studentId
            """)
    List<ProblemAttempt> findAllWithProblemAndTags(
            @Param("studentId")
            Long studentId
    );
}