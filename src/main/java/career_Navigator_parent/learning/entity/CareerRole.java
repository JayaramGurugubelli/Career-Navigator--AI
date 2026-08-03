package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.CareerDifficulty;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "learning_career_roles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_learning_career_role_slug",
                        columnNames = "slug"
                )
        },
        indexes = {
                @Index(
                        name = "idx_learning_role_domain",
                        columnList = "career_domain_id"
                ),
                @Index(
                        name = "idx_learning_role_active",
                        columnList = "active"
                ),
                @Index(
                        name = "idx_learning_role_featured",
                        columnList = "featured"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerRole extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "career_domain_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_learning_role_domain"
            )
    )
    private CareerDomain domain;

    @Column(
            nullable = false,
            length = 180
    )
    private String title;

    @Column(
            nullable = false,
            length = 200
    )
    private String slug;

    @Lob
    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String summary;

    @Lob
    @Column(
            name = "responsibilities",
            columnDefinition = "TEXT"
    )
    private String responsibilities;

    @Lob
    @Column(
            name = "work_environment",
            columnDefinition = "TEXT"
    )
    private String workEnvironment;

    @Lob
    @Column(
            name = "entry_level_titles",
            columnDefinition = "TEXT"
    )
    private String entryLevelTitles;

    @Lob
    @Column(
            name = "career_outlook",
            columnDefinition = "TEXT"
    )
    private String careerOutlook;

    @Column(
            name = "minimum_qualification",
            length = 500
    )
    private String minimumQualification;

    @Column(
            name = "average_learning_months"
    )
    private Integer averageLearningMonths;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 40
    )
    private CareerDifficulty difficulty;

    @Column(
            name = "thumbnail_url",
            length = 1000
    )
    private String thumbnailUrl;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "learning_career_role_disciplines",
            joinColumns = @JoinColumn(
                    name = "career_role_id",
                    foreignKey = @ForeignKey(
                            name = "fk_role_discipline_role"
                    )
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "discipline_id",
                    foreignKey = @ForeignKey(
                            name = "fk_role_discipline_discipline"
                    )
            ),
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_role_discipline",
                            columnNames = {
                                    "career_role_id",
                                    "discipline_id"
                            }
                    )
            }
    )
    @Builder.Default
    private Set<AcademicDiscipline> eligibleDisciplines =
            new LinkedHashSet<>();

    @Version
    @Column(nullable = false)
    private Long version;

    public void addDiscipline(
            AcademicDiscipline discipline
    ) {
        if (discipline != null) {
            eligibleDisciplines.add(discipline);
        }
    }

    public void removeDiscipline(
            AcademicDiscipline discipline
    ) {
        eligibleDisciplines.remove(discipline);
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
                averageLearningMonths != null
                        && averageLearningMonths < 0
        ) {
            averageLearningMonths = 0;
        }

        if (featured == null) {
            featured = false;
        }

        if (active == null) {
            active = true;
        }
    }
}