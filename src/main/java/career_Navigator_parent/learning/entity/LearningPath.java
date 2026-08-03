package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.ContentStatus;
import career_Navigator_parent.learning.enums.LearningLevel;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(
        name = "learning_paths",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_learning_path_slug_version",
                        columnNames = {
                                "slug",
                                "path_version"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_learning_path_role",
                        columnList = "career_role_id"
                ),
                @Index(
                        name = "idx_learning_path_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_learning_path_active",
                        columnList = "active"
                ),
                @Index(
                        name = "idx_learning_path_featured",
                        columnList = "featured"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPath extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "career_role_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_learning_path_role"
            )
    )
    private CareerRole careerRole;

    @Column(
            nullable = false,
            length = 200
    )
    private String title;

    @Column(
            nullable = false,
            length = 220
    )
    private String slug;

    @Lob
    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private LearningLevel level;

    @Column(
            name = "estimated_duration_hours",
            nullable = false
    )
    private Integer estimatedDurationHours;

    @Column(
            name = "thumbnail_url",
            length = 1000
    )
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private ContentStatus status = ContentStatus.DRAFT;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean premium = false;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean featured = false;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;

    @Column(
            name = "path_version",
            nullable = false
    )
    @Builder.Default
    private Integer pathVersion = 1;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "learning_path_disciplines",
            joinColumns = @JoinColumn(
                    name = "learning_path_id",
                    foreignKey = @ForeignKey(
                            name = "fk_path_discipline_path"
                    )
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "discipline_id",
                    foreignKey = @ForeignKey(
                            name = "fk_path_discipline_discipline"
                    )
            ),
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_path_discipline",
                            columnNames = {
                                    "learning_path_id",
                                    "discipline_id"
                            }
                    )
            }
    )
    @Builder.Default
    private Set<AcademicDiscipline> disciplines =
            new LinkedHashSet<>();

    @OneToMany(
            mappedBy = "learningPath",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("sequenceNumber ASC")
    @Builder.Default
    private List<LearningPathMilestone> milestones =
            new ArrayList<>();

    @Version
    @Column(nullable = false)
    private Long version;

    public void addDiscipline(
            AcademicDiscipline discipline
    ) {
        if (discipline != null) {
            disciplines.add(discipline);
        }
    }

    public void addMilestone(
            LearningPathMilestone milestone
    ) {
        if (milestone == null) {
            return;
        }

        milestone.setLearningPath(this);
        milestones.add(milestone);
    }

    public void removeMilestone(
            LearningPathMilestone milestone
    ) {
        if (milestone == null) {
            return;
        }

        milestones.remove(milestone);
        milestone.setLearningPath(null);
    }

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (title != null) {
            title = title.strip();
        }

        if (slug != null) {
            slug = slug
                    .strip()
                    .toLowerCase();
        }

        if (
                estimatedDurationHours == null
                        || estimatedDurationHours < 1
        ) {
            estimatedDurationHours = 1;
        }

        if (
                pathVersion == null
                        || pathVersion < 1
        ) {
            pathVersion = 1;
        }

        if (status == null) {
            status = ContentStatus.DRAFT;
        }

        if (premium == null) {
            premium = false;
        }

        if (featured == null) {
            featured = false;
        }

        if (active == null) {
            active = true;
        }
    }
}