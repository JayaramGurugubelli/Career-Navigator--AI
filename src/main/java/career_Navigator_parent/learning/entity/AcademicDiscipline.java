package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "learning_academic_disciplines",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_learning_discipline_code",
                        columnNames = "code"
                ),
                @UniqueConstraint(
                        name = "uk_learning_discipline_name",
                        columnNames = "name"
                )
        },
        indexes = {
                @Index(
                        name = "idx_learning_discipline_active",
                        columnList = "active"
                ),
                @Index(
                        name = "idx_learning_discipline_order",
                        columnList = "display_order"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicDiscipline extends BaseEntity {

    @Column(
            nullable = false,
            length = 120
    )
    private String name;

    @Column(
            nullable = false,
            length = 30
    )
    private String code;

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

        if (code != null) {
            code = code
                    .strip()
                    .toUpperCase();
        }

        if (displayOrder == null || displayOrder < 0) {
            displayOrder = 0;
        }

        if (active == null) {
            active = true;
        }
    }
}