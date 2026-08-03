package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.LearningGoalStatus;
import career_Navigator_parent.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "student_weekly_learning_goals",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_learning_goal_week",
                        columnNames = {
                                "student_id",
                                "week_start_date"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_weekly_goal_student",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_weekly_goal_week",
                        columnList = "week_start_date, week_end_date"
                ),
                @Index(
                        name = "idx_weekly_goal_status",
                        columnList = "student_id, status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentWeeklyLearningGoal
        extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private Student student;

    @Column(
            name = "week_start_date",
            nullable = false
    )
    private LocalDate weekStartDate;

    @Column(
            name = "week_end_date",
            nullable = false
    )
    private LocalDate weekEndDate;

    @Column(
            name = "target_minutes",
            nullable = false
    )
    private Integer targetMinutes;

    @Column(
            name = "completed_minutes",
            nullable = false
    )
    @Builder.Default
    private Integer completedMinutes = 0;

    @Column(
            name = "target_lessons",
            nullable = false
    )
    @Builder.Default
    private Integer targetLessons = 0;

    @Column(
            name = "completed_lessons",
            nullable = false
    )
    @Builder.Default
    private Integer completedLessons = 0;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private LearningGoalStatus status =
            LearningGoalStatus.ACTIVE;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    public void recordLearning(
            int minutes,
            int lessons
    ) {
        completedMinutes =
                Math.addExact(
                        completedMinutes == null
                                ? 0
                                : completedMinutes,
                        Math.max(minutes, 0)
                );

        completedLessons =
                Math.addExact(
                        completedLessons == null
                                ? 0
                                : completedLessons,
                        Math.max(lessons, 0)
                );

        evaluateStatus();
    }

    public void evaluateStatus() {

        boolean minutesReached =
                targetMinutes <= 0
                        || completedMinutes >= targetMinutes;

        boolean lessonsReached =
                targetLessons <= 0
                        || completedLessons >= targetLessons;

        if (minutesReached && lessonsReached) {
            status = LearningGoalStatus.COMPLETED;

            if (completedAt == null) {
                completedAt = LocalDateTime.now();
            }
        }
    }

    @PrePersist
    @PreUpdate
    private void validateAndNormalize() {

        if (
                weekStartDate != null
                        && weekEndDate == null
        ) {
            weekEndDate =
                    weekStartDate.plusDays(6);
        }

        if (
                weekStartDate != null
                        && weekEndDate != null
                        && weekEndDate.isBefore(weekStartDate)
        ) {
            throw new IllegalStateException(
                    "Week end date cannot be before week start date."
            );
        }

        targetMinutes =
                Math.max(
                        targetMinutes == null
                                ? 0
                                : targetMinutes,
                        0
                );

        targetLessons =
                Math.max(
                        targetLessons == null
                                ? 0
                                : targetLessons,
                        0
                );

        completedMinutes =
                Math.max(
                        completedMinutes == null
                                ? 0
                                : completedMinutes,
                        0
                );

        completedLessons =
                Math.max(
                        completedLessons == null
                                ? 0
                                : completedLessons,
                        0
                );

        if (status == null) {
            status = LearningGoalStatus.ACTIVE;
        }
    }
}