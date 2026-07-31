package career_Navigator_parent.coding.entity;

import career_Navigator_parent.coding.enums.TestCaseVisibility;
import career_Navigator_parent.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "coding_problem_test_cases",
        indexes = {
                @Index(
                        name = "idx_test_case_problem_visibility",
                        columnList = "problem_id, visibility"
                ),
                @Index(
                        name = "idx_test_case_problem_active",
                        columnList = "problem_id, active"
                ),
                @Index(
                        name = "idx_test_case_problem_generated",
                        columnList = "problem_id, generated_case"
                ),
                @Index(
                        name = "idx_test_case_input_hash",
                        columnList = "problem_id, input_hash"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_problem_display_order",
                        columnNames = {
                                "problem_id",
                                "display_order"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemTestCase extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "problem_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_test_case_problem"
            )
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CodingProblem problem;

    @Lob
    @Column(
            name = "input_data",
            nullable = false,
            columnDefinition = "LONGTEXT"
    )
    private String input;

    @Lob
    @Column(
            name = "expected_output",
            nullable = false,
            columnDefinition = "LONGTEXT"
    )
    private String expectedOutput;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "visibility",
            nullable = false,
            length = 30
    )
    private TestCaseVisibility visibility;

    @Column(
            name = "display_order",
            nullable = false
    )
    private Integer displayOrder;

    @Column(
            name = "score_weight",
            nullable = false
    )
    @Builder.Default
    private Double scoreWeight = 1.0;

    @Column(
            name = "custom_time_limit_seconds"
    )
    private Double customTimeLimitSeconds;

    @Column(
            name = "custom_memory_limit_megabytes"
    )
    private Integer customMemoryLimitMegabytes;

    @Column(
            name = "input_hash",
            length = 64
    )
    private String inputHash;

    @Column(
            name = "expected_output_hash",
            length = 64
    )
    private String expectedOutputHash;

    @Column(
            name = "generated_case",
            nullable = false
    )
    @Builder.Default
    private Boolean generatedCase = false;

    @Column(
            name = "generator_seed"
    )
    private Long generatorSeed;

    @Column(
            name = "active",
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;
}