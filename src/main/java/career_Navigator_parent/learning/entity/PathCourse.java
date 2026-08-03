package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.UnlockRule;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "learning_path_courses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_path_course",
                        columnNames = {
                                "learning_path_id",
                                "course_id"
                        }
                ),
                @UniqueConstraint(
                        name = "uk_path_course_sequence",
                        columnNames = {
                                "learning_path_id",
                                "sequence_number"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_path_course_path",
                        columnList = "learning_path_id"
                ),
                @Index(
                        name = "idx_path_course_milestone",
                        columnList = "milestone_id"
                ),
                @Index(
                        name = "idx_path_course_course",
                        columnList = "course_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PathCourse extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "learning_path_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_path_course_path"
            )
    )
    private LearningPath learningPath;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "milestone_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_path_course_milestone"
            )
    )
    private LearningPathMilestone milestone;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "course_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_path_course_course"
            )
    )
    private Course course;

    @Column(
            name = "sequence_number",
            nullable = false
    )
    private Integer sequenceNumber;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean mandatory = true;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "unlock_rule",
            nullable = false,
            length = 50
    )
    @Builder.Default
    private UnlockRule unlockRule =
            UnlockRule.PREVIOUS_COURSE_COMPLETED;

    @Column(
            name = "minimum_score"
    )
    private Double minimumScore;

    @Column(
            name = "scheduled_release_at"
    )
    private LocalDateTime scheduledReleaseAt;

    @Column(
            name = "estimated_hours_override"
    )
    private Integer estimatedHoursOverride;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    @PreUpdate
    private void validateAndNormalize() {

        if (
                sequenceNumber == null
                        || sequenceNumber < 1
        ) {
            sequenceNumber = 1;
        }

        if (
                minimumScore != null
                        && (
                        minimumScore < 0
                                || minimumScore > 100
                )
        ) {
            throw new IllegalStateException(
                    "Minimum score must be between 0 and 100."
            );
        }

        if (
                estimatedHoursOverride != null
                        && estimatedHoursOverride < 1
        ) {
            estimatedHoursOverride = 1;
        }

        if (mandatory == null) {
            mandatory = true;
        }

        if (unlockRule == null) {
            unlockRule =
                    UnlockRule.PREVIOUS_COURSE_COMPLETED;
        }

        if (active == null) {
            active = true;
        }

        if (
                milestone != null
                        && learningPath != null
                        && milestone.getLearningPath() != null
                        && !milestone
                        .getLearningPath()
                        .getId()
                        .equals(learningPath.getId())
        ) {
            throw new IllegalStateException(
                    "The milestone must belong to the selected learning path."
            );
        }
    }
}