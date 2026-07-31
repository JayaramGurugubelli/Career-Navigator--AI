package career_Navigator_parent.coding.entity;

import career_Navigator_parent.coding.enums.ProblemAttemptStatus;
import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "problem_attempts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cp_problem_attempt_student_problem",
                        columnNames = {
                                "student_id",
                                "problem_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_cp_problem_attempt_student",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_cp_problem_attempt_problem",
                        columnList = "problem_id"
                ),
                @Index(
                        name = "idx_cp_problem_attempt_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_cp_problem_attempt_student_status",
                        columnList = "student_id,status"
                ),
                @Index(
                        name = "idx_cp_problem_attempt_last_attempted",
                        columnList = "last_attempted_at"
                ),
                @Index(
                        name = "idx_cp_problem_attempt_solved_at",
                        columnList = "solved_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemAttempt extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_cp_problem_attempt_student"
            )
    )
    private Student student;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "problem_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_cp_problem_attempt_problem"
            )
    )
    private CodingProblem problem;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private ProblemAttemptStatus status =
            ProblemAttemptStatus.ATTEMPTED;

    @Column(
            name = "attempt_count",
            nullable = false
    )
    @Builder.Default
    private Integer attemptCount = 0;

    @Column(
            name = "accepted_submission_count",
            nullable = false
    )
    @Builder.Default
    private Integer acceptedSubmissionCount = 0;

    @Column(name = "best_score")
    private Integer bestScore;

    @Column(name = "best_runtime_milliseconds")
    private Long bestRuntimeMilliseconds;

    @Column(name = "best_memory_kilobytes")
    private Long bestMemoryKilobytes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "best_submission_id",
            foreignKey = @ForeignKey(
                    name = "fk_cp_problem_attempt_best_submission"
            )
    )
    private CodeSubmission bestSubmission;

    @Column(
            name = "first_attempted_at",
            nullable = false
    )
    private LocalDateTime firstAttemptedAt;

    @Column(
            name = "last_attempted_at",
            nullable = false
    )
    private LocalDateTime lastAttemptedAt;

    @Column(name = "solved_at")
    private LocalDateTime solvedAt;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version;

    @PrePersist
    public void initializeDefaults() {
        LocalDateTime now = LocalDateTime.now();

        if (status == null) {
            status = ProblemAttemptStatus.ATTEMPTED;
        }

        if (attemptCount == null) {
            attemptCount = 0;
        }

        if (acceptedSubmissionCount == null) {
            acceptedSubmissionCount = 0;
        }

        if (firstAttemptedAt == null) {
            firstAttemptedAt = now;
        }

        if (lastAttemptedAt == null) {
            lastAttemptedAt = now;
        }

        if (version == null) {
            version = 0L;
        }
    }

    public void recordQueuedSubmission() {
        LocalDateTime now = LocalDateTime.now();

        if (firstAttemptedAt == null) {
            firstAttemptedAt = now;
        }

        lastAttemptedAt = now;

        if (status == null) {
            status = ProblemAttemptStatus.ATTEMPTED;
        }
    }

    public void recordCompletedAttempt() {
        attemptCount =
                safeIncrement(attemptCount);

        lastAttemptedAt =
                LocalDateTime.now();

        if (firstAttemptedAt == null) {
            firstAttemptedAt =
                    lastAttemptedAt;
        }
    }

    public void recordAcceptedSubmission(
            CodeSubmission submission,
            Integer score,
            Long runtimeMilliseconds,
            Long memoryKilobytes
    ) {
        LocalDateTime now = LocalDateTime.now();

        acceptedSubmissionCount =
                safeIncrement(
                        acceptedSubmissionCount
                );

        status = ProblemAttemptStatus.SOLVED;
        lastAttemptedAt = now;

        if (solvedAt == null) {
            solvedAt = now;
        }

        boolean betterScore =
                score != null
                        && (
                        bestScore == null
                                || score > bestScore
                );

        boolean sameScoreButFaster =
                score != null
                        && bestScore != null
                        && score.equals(bestScore)
                        && runtimeMilliseconds != null
                        && (
                        bestRuntimeMilliseconds == null
                                || runtimeMilliseconds
                                < bestRuntimeMilliseconds
                );

        boolean sameScoreRuntimeLessMemory =
                score != null
                        && bestScore != null
                        && score.equals(bestScore)
                        && runtimeMilliseconds != null
                        && bestRuntimeMilliseconds != null
                        && runtimeMilliseconds.equals(
                        bestRuntimeMilliseconds
                )
                        && memoryKilobytes != null
                        && (
                        bestMemoryKilobytes == null
                                || memoryKilobytes
                                < bestMemoryKilobytes
                );

        if (
                bestSubmission == null
                        || betterScore
                        || sameScoreButFaster
                        || sameScoreRuntimeLessMemory
        ) {
            bestSubmission = submission;
        }

        if (
                score != null
                        && (
                        bestScore == null
                                || betterScore
                )
        ) {
            bestScore = score;
        }

        if (
                runtimeMilliseconds != null
                        && runtimeMilliseconds >= 0
                        && (
                        bestRuntimeMilliseconds == null
                                || runtimeMilliseconds
                                < bestRuntimeMilliseconds
                )
        ) {
            bestRuntimeMilliseconds =
                    runtimeMilliseconds;
        }

        if (
                memoryKilobytes != null
                        && memoryKilobytes >= 0
                        && (
                        bestMemoryKilobytes == null
                                || memoryKilobytes
                                < bestMemoryKilobytes
                )
        ) {
            bestMemoryKilobytes =
                    memoryKilobytes;
        }
    }

    public void preserveSolvedStatusOrMarkAttempted() {
        if (status != ProblemAttemptStatus.SOLVED) {
            status = ProblemAttemptStatus.ATTEMPTED;
        }
    }

    private int safeIncrement(
            Integer currentValue
    ) {
        if (currentValue == null) {
            return 1;
        }

        return Math.addExact(
                currentValue,
                1
        );
    }
}