package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.ProgressStatus;
import career_Navigator_parent.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "student_course_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_course_progress",
                        columnNames = {
                                "student_id",
                                "course_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_course_progress_student",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_course_progress_course",
                        columnList = "course_id"
                ),
                @Index(
                        name = "idx_course_progress_status",
                        columnList = "student_id, status"
                ),
                @Index(
                        name = "idx_course_progress_last_access",
                        columnList = "last_accessed_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCourseProgress extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_course_progress_student"
            )
    )
    private Student student;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "course_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_course_progress_course"
            )
    )
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private ProgressStatus status =
            ProgressStatus.NOT_STARTED;

    @Column(
            name = "completed_lessons",
            nullable = false
    )
    @Builder.Default
    private Integer completedLessons = 0;

    @Column(
            name = "total_lessons",
            nullable = false
    )
    @Builder.Default
    private Integer totalLessons = 0;

    @Column(
            name = "progress_percentage",
            nullable = false
    )
    @Builder.Default
    private Double progressPercentage = 0.0;

    @Column(
            name = "best_assessment_score"
    )
    private Double bestAssessmentScore;

    @Column(
            name = "total_time_spent_seconds",
            nullable = false
    )
    @Builder.Default
    private Long totalTimeSpentSeconds = 0L;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    public void updateLessonProgress(
            int completed,
            int total
    ) {
        completedLessons = Math.max(completed, 0);
        totalLessons = Math.max(total, 0);

        progressPercentage =
                totalLessons == 0
                        ? 0.0
                        : Math.min(
                        100.0,
                        completedLessons * 100.0
                                / totalLessons
                );

        if (
                progressPercentage > 0
                        && status
                        == ProgressStatus.NOT_STARTED
        ) {
            status = ProgressStatus.IN_PROGRESS;
            startedAt = LocalDateTime.now();
        }

        if (
                totalLessons > 0
                        && completedLessons >= totalLessons
        ) {
            status = ProgressStatus.COMPLETED;
            progressPercentage = 100.0;
            completedAt = LocalDateTime.now();
        }

        lastAccessedAt = LocalDateTime.now();
    }

    public void recordAssessmentScore(
            Double score
    ) {
        if (score == null || score < 0) {
            return;
        }

        if (
                bestAssessmentScore == null
                        || score > bestAssessmentScore
        ) {
            bestAssessmentScore = score;
        }
    }

    public void addTimeSpent(
            long seconds
    ) {
        if (seconds <= 0) {
            return;
        }

        totalTimeSpentSeconds =
                Math.addExact(
                        totalTimeSpentSeconds == null
                                ? 0L
                                : totalTimeSpentSeconds,
                        seconds
                );
    }
}