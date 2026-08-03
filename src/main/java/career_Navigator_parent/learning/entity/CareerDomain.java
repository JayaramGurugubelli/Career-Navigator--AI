package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "learning_career_domains",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_learning_career_domain_slug",
                        columnNames = "slug"
                ),
                @UniqueConstraint(
                        name = "uk_learning_career_domain_name",
                        columnNames = "name"
                )
        },
        indexes = {
                @Index(
                        name = "idx_learning_domain_active",
                        columnList = "active"
                ),
                @Index(
                        name = "idx_learning_domain_order",
                        columnList = "display_order"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerDomain extends BaseEntity {

    @Column(
            nullable = false,
            length = 150
    )
    private String name;

    @Column(
            nullable = false,
            length = 180
    )
    private String slug;

    @Lob
    @Column(
            columnDefinition = "TEXT"
    )
    private String description;

    @Column(
            name = "icon_url",
            length = 1000
    )
    private String iconUrl;

    @Column(
            name = "display_order",
            nullable = false
    )
    @Builder.Default
    private Integer displayOrder = 0;

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
        if (name != null) {
            name = name.strip();
        }

        if (slug != null) {
            slug = slug
                    .strip()
                    .toLowerCase();
        }

        if (displayOrder == null || displayOrder < 0) {
            displayOrder = 0;
        }

        if (active == null) {
            active = true;
        }
    }
}