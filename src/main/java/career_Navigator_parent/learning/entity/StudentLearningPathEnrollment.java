package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.EnrollmentStatus;
import career_Navigator_parent.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "student_learning_path_enrollments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_learning_path",
                        columnNames = {
                                "student_id",
                                "learning_path_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_path_enrollment_student",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_path_enrollment_path",
                        columnList = "learning_path_id"
                ),
                @Index(
                        name = "idx_path_enrollment_status",
                        columnList = "student_id, status"
                ),
                @Index(
                        name = "idx_path_enrollment_last_access",
                        columnList = "last_accessed_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentLearningPathEnrollment
        extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_path_enrollment_student"
            )
    )
    private Student student;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "learning_path_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_path_enrollment_path"
            )
    )
    private LearningPath learningPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "current_milestone_id",
            foreignKey = @ForeignKey(
                    name = "fk_path_enrollment_current_milestone"
            )
    )
    private LearningPathMilestone currentMilestone;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private EnrollmentStatus status =
            EnrollmentStatus.ENROLLED;

    @Column(
            name = "progress_percentage",
            nullable = false
    )
    @Builder.Default
    private Double progressPercentage = 0.0;

    @Column(
            name = "completed_courses",
            nullable = false
    )
    @Builder.Default
    private Integer completedCourses = 0;

    @Column(
            name = "total_courses",
            nullable = false
    )
    @Builder.Default
    private Integer totalCourses = 0;

    @Column(
            name = "enrolled_at",
            nullable = false
    )
    private LocalDateTime enrolledAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "paused_at")
    private LocalDateTime pausedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    private void initializeEnrollment() {

        if (enrolledAt == null) {
            enrolledAt = LocalDateTime.now();
        }

        if (lastAccessedAt == null) {
            lastAccessedAt = enrolledAt;
        }

        normalizeProgress();
    }

    @PreUpdate
    private void normalizeProgress() {
        progressPercentage =
                clampPercentage(progressPercentage);

        completedCourses =
                Math.max(
                        completedCourses == null
                                ? 0
                                : completedCourses,
                        0
                );

        totalCourses =
                Math.max(
                        totalCourses == null
                                ? 0
                                : totalCourses,
                        0
                );

        if (status == null) {
            status = EnrollmentStatus.ENROLLED;
        }
    }

    public void start() {
        status = EnrollmentStatus.IN_PROGRESS;

        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }

        lastAccessedAt = LocalDateTime.now();
        pausedAt = null;
    }

    public void pause() {
        status = EnrollmentStatus.PAUSED;
        pausedAt = LocalDateTime.now();
    }

    public void complete() {
        status = EnrollmentStatus.COMPLETED;
        progressPercentage = 100.0;
        completedAt = LocalDateTime.now();
        lastAccessedAt = completedAt;
    }

    public void updateProgress(
            int completed,
            int total
    ) {
        completedCourses =
                Math.max(completed, 0);

        totalCourses =
                Math.max(total, 0);

        progressPercentage =
                totalCourses == 0
                        ? 0.0
                        : Math.min(
                        100.0,
                        completedCourses * 100.0
                                / totalCourses
                );

        lastAccessedAt = LocalDateTime.now();

        if (
                progressPercentage > 0
                        && status
                        == EnrollmentStatus.ENROLLED
        ) {
            start();
        }

        if (
                totalCourses > 0
                        && completedCourses >= totalCourses
        ) {
            complete();
        }
    }

    private double clampPercentage(
            Double value
    ) {
        if (value == null) {
            return 0.0;
        }

        return Math.max(
                0.0,
                Math.min(100.0, value)
        );
    }
}