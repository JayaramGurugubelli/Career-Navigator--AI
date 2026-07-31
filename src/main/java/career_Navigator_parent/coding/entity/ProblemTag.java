package career_Navigator_parent.coding.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "coding_problem_tags",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_coding_problem_tag_name",
                        columnNames = "name"
                ),
                @UniqueConstraint(
                        name = "uk_coding_problem_tag_slug",
                        columnNames = "slug"
                )
        },
        indexes = {
                @Index(
                        name = "idx_coding_problem_tag_active",
                        columnList = "active"
                ),
                @Index(
                        name = "idx_coding_problem_tag_name",
                        columnList = "name"
                ),
                @Index(
                        name = "idx_coding_problem_tag_slug",
                        columnList = "slug"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemTag extends BaseEntity {

    @Column(
            nullable = false,
            length = 80
    )
    private String name;

    @Column(
            nullable = false,
            length = 100
    )
    private String slug;

    @Column(length = 300)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CodingProblem> problems = new HashSet<>();
}