package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.ProgressStatus;
import career_Navigator_parent.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "student_lesson_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_lesson_progress",
                        columnNames = {
                                "student_id",
                                "lesson_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_lesson_progress_student",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_lesson_progress_lesson",
                        columnList = "lesson_id"
                ),
                @Index(
                        name = "idx_lesson_progress_status",
                        columnList = "student_id, status"
                ),
                @Index(
                        name = "idx_lesson_progress_course",
                        columnList = "course_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentLessonProgress extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_lesson_progress_student"
            )
    )
    private Student student;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "lesson_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_lesson_progress_lesson"
            )
    )
    private Lesson lesson;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "course_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_lesson_progress_course"
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
            name = "progress_percentage",
            nullable = false
    )
    @Builder.Default
    private Double progressPercentage = 0.0;

    @Column(
            name = "time_spent_seconds",
            nullable = false
    )
    @Builder.Default
    private Long timeSpentSeconds = 0L;

    @Column(
            name = "last_position_seconds",
            nullable = false
    )
    @Builder.Default
    private Long lastPositionSeconds = 0L;

    @Column(
            name = "completion_count",
            nullable = false
    )
    @Builder.Default
    private Integer completionCount = 0;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    public void start() {

        if (status == ProgressStatus.NOT_STARTED) {
            status = ProgressStatus.IN_PROGRESS;
            startedAt = LocalDateTime.now();
        }

        lastAccessedAt = LocalDateTime.now();
    }

    public void recordProgress(
            double percentage,
            long positionSeconds,
            long additionalTimeSeconds
    ) {
        start();

        progressPercentage =
                Math.max(
                        0.0,
                        Math.min(100.0, percentage)
                );

        lastPositionSeconds =
                Math.max(positionSeconds, 0L);

        if (additionalTimeSeconds > 0) {
            timeSpentSeconds =
                    Math.addExact(
                            timeSpentSeconds == null
                                    ? 0L
                                    : timeSpentSeconds,
                            additionalTimeSeconds
                    );
        }

        if (progressPercentage >= 100.0) {
            complete();
        }
    }

    public void complete() {
        status = ProgressStatus.COMPLETED;
        progressPercentage = 100.0;
        completedAt = LocalDateTime.now();
        lastAccessedAt = completedAt;

        completionCount =
                Math.addExact(
                        completionCount == null
                                ? 0
                                : completionCount,
                        1
                );
    }
}