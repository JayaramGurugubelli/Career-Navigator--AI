package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.ProviderType;
import career_Navigator_parent.learning.enums.ResourceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "learning_resources",
        indexes = {
                @Index(
                        name = "idx_resource_course",
                        columnList = "course_id"
                ),
                @Index(
                        name = "idx_resource_lesson",
                        columnList = "lesson_id"
                ),
                @Index(
                        name = "idx_resource_milestone",
                        columnList = "milestone_id"
                ),
                @Index(
                        name = "idx_resource_project",
                        columnList = "project_id"
                ),
                @Index(
                        name = "idx_resource_type",
                        columnList = "resource_type"
                ),
                @Index(
                        name = "idx_resource_active",
                        columnList = "active"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningResource extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "course_id",
            foreignKey = @ForeignKey(
                    name = "fk_resource_course"
            )
    )
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "lesson_id",
            foreignKey = @ForeignKey(
                    name = "fk_resource_lesson"
            )
    )
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "milestone_id",
            foreignKey = @ForeignKey(
                    name = "fk_resource_milestone"
            )
    )
    private LearningPathMilestone milestone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_id",
            foreignKey = @ForeignKey(
                    name = "fk_resource_project"
            )
    )
    private LearningProject project;

    @Column(
            nullable = false,
            length = 250
    )
    private String title;

    @Lob
    @Column(
            columnDefinition = "TEXT"
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "resource_type",
            nullable = false,
            length = 40
    )
    private ResourceType resourceType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "provider_type",
            nullable = false,
            length = 50
    )
    private ProviderType providerType;

    @Column(
            name = "provider_name",
            length = 180
    )
    private String providerName;

    @Column(
            name = "resource_url",
            nullable = false,
            length = 1800
    )
    private String resourceUrl;

    @Column(
            name = "thumbnail_url",
            length = 1500
    )
    private String thumbnailUrl;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean free = true;

    @Column(
            name = "sequence_number",
            nullable = false
    )
    @Builder.Default
    private Integer sequenceNumber = 1;

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

        if (free == null) {
            free = true;
        }

        if (active == null) {
            active = true;
        }

        validateOwner();
    }

    private void validateOwner() {

        int ownerCount = 0;

        if (course != null) {
            ownerCount++;
        }

        if (lesson != null) {
            ownerCount++;
        }

        if (milestone != null) {
            ownerCount++;
        }

        if (project != null) {
            ownerCount++;
        }

        if (ownerCount != 1) {
            throw new IllegalStateException(
                    "A learning resource must belong to exactly one owner."
            );
        }
    }
}