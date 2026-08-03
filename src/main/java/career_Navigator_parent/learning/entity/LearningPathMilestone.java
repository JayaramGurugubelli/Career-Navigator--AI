package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.MilestoneType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "learning_path_milestones",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_path_milestone_sequence",
                        columnNames = {
                                "learning_path_id",
                                "sequence_number"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_milestone_path",
                        columnList = "learning_path_id"
                ),
                @Index(
                        name = "idx_milestone_type",
                        columnList = "milestone_type"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPathMilestone extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "learning_path_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_milestone_learning_path"
            )
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LearningPath learningPath;

    @Column(
            nullable = false,
            length = 180
    )
    private String title;

    @Lob
    @Column(
            columnDefinition = "TEXT"
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "milestone_type",
            nullable = false,
            length = 40
    )
    private MilestoneType milestoneType;

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

    @Column(
            name = "estimated_hours",
            nullable = false
    )
    @Builder.Default
    private Integer estimatedHours = 1;

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
    private void normalize() {
        if (title != null) {
            title = title.strip();
        }

        if (
                sequenceNumber == null
                        || sequenceNumber < 1
        ) {
            sequenceNumber = 1;
        }

        if (
                estimatedHours == null
                        || estimatedHours < 1
        ) {
            estimatedHours = 1;
        }

        if (mandatory == null) {
            mandatory = true;
        }

        if (active == null) {
            active = true;
        }
    }
}