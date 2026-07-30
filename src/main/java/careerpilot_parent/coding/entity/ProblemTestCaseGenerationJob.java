package careerpilot_parent.coding.entity;

import careerpilot_parent.coding.enums.ProgrammingLanguage;
import careerpilot_parent.coding.enums.TestCaseGenerationStatus;
import careerpilot_parent.coding.enums.TestCaseGeneratorType;
import careerpilot_parent.coding.enums.TestCaseVisibility;
import careerpilot_parent.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "coding_test_case_generation_jobs",
        indexes = {
                @Index(
                        name = "idx_generation_job_problem",
                        columnList = "problem_id"
                ),
                @Index(
                        name = "idx_generation_job_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_generation_job_problem_status",
                        columnList = "problem_id, status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemTestCaseGenerationJob
        extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "problem_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_generation_job_problem"
            )
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CodingProblem problem;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "generator_type",
            nullable = false,
            length = 50
    )
    private TestCaseGeneratorType generatorType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 40
    )
    private TestCaseGenerationStatus status;

    @Column(
            name = "requested_cases",
            nullable = false
    )
    private Integer requestedCases;

    @Column(
            name = "processed_cases",
            nullable = false
    )
    @Builder.Default
    private Integer processedCases = 0;

    @Column(
            name = "generated_cases",
            nullable = false
    )
    @Builder.Default
    private Integer generatedCases = 0;

    @Column(
            name = "failed_cases",
            nullable = false
    )
    @Builder.Default
    private Integer failedCases = 0;

    @Column(
            name = "random_seed",
            nullable = false
    )
    private Long randomSeed;

    @Column(
            name = "minimum_array_length",
            nullable = false
    )
    private Integer minimumArrayLength;

    @Column(
            name = "maximum_array_length",
            nullable = false
    )
    private Integer maximumArrayLength;

    @Column(
            name = "minimum_value",
            nullable = false
    )
    private Long minimumValue;

    @Column(
            name = "maximum_value",
            nullable = false
    )
    private Long maximumValue;

    @Column(
            name = "include_edge_cases",
            nullable = false
    )
    private Boolean includeEdgeCases;

    @Column(
            name = "include_performance_cases",
            nullable = false
    )
    private Boolean includePerformanceCases;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "visibility",
            nullable = false,
            length = 30
    )
    private TestCaseVisibility visibility;

    @Column(
            name = "batch_size",
            nullable = false
    )
    private Integer batchSize;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "reference_language",
            nullable = false,
            length = 30
    )
    private ProgrammingLanguage referenceLanguage;

    @Column(
            name = "reference_solution_id",
            nullable = false
    )
    private Long referenceSolutionId;

    @Lob
    @Column(
            name = "input_template",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String inputTemplate;

    @Column(
            name = "minimum_target_value"
    )
    private Long minimumTargetValue;

    @Column(
            name = "maximum_target_value"
    )
    private Long maximumTargetValue;

    @Column(
            name = "score_weight",
            nullable = false
    )
    private Double scoreWeight;

    @Column(
            name = "custom_time_limit_seconds"
    )
    private Double customTimeLimitSeconds;

    @Column(
            name = "custom_memory_limit_megabytes"
    )
    private Integer customMemoryLimitMegabytes;

    @Column(
            name = "message",
            length = 500
    )
    private String message;

    @Lob
    @Column(
            name = "failure_reason",
            columnDefinition = "TEXT"
    )
    private String failureReason;

    @Column(
            name = "queued_at",
            nullable = false
    )
    private LocalDateTime queuedAt;

    @Column(
            name = "started_at"
    )
    private LocalDateTime startedAt;

    @Column(
            name = "completed_at"
    )
    private LocalDateTime completedAt;
}