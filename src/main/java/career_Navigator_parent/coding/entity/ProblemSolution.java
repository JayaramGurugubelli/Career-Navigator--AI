package career_Navigator_parent.coding.entity;

import career_Navigator_parent.coding.enums.ProgrammingLanguage;
import career_Navigator_parent.coding.enums.SolutionApproach;
import career_Navigator_parent.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "coding_problem_solutions",
        indexes = {
                @Index(
                        name = "idx_solution_problem",
                        columnList = "problem_id"
                ),
                @Index(
                        name = "idx_solution_problem_active",
                        columnList = "problem_id, active"
                ),
                @Index(
                        name = "idx_solution_problem_official",
                        columnList = "problem_id, official"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemSolution extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "problem_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_solution_problem"
            )
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CodingProblem problem;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "programming_language",
            nullable = false,
            length = 30
    )
    private ProgrammingLanguage programmingLanguage;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "approach",
            nullable = false,
            length = 50
    )
    private SolutionApproach approach;

    @Column(
            name = "title",
            nullable = false,
            length = 200
    )
    private String title;

    @Lob
    @Column(
            name = "explanation",
            nullable = false,
            columnDefinition = "LONGTEXT"
    )
    private String explanation;

    @Lob
    @Column(
            name = "source_code",
            nullable = false,
            columnDefinition = "LONGTEXT"
    )
    private String sourceCode;

    @Column(
            name = "time_complexity",
            nullable = false,
            length = 100
    )
    private String timeComplexity;

    @Column(
            name = "space_complexity",
            nullable = false,
            length = 100
    )
    private String spaceComplexity;

    @Column(
            name = "official",
            nullable = false
    )
    @Builder.Default
    private Boolean official = false;

    @Column(
            name = "active",
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;
}