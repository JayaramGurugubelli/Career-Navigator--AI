package career_Navigator_parent.coding.entity;

import career_Navigator_parent.coding.enums.SubmissionStatus;
import career_Navigator_parent.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "coding_submission_test_case_results",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_submission_test_case_result",
                        columnNames = {
                                "submission_id",
                                "test_case_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_test_result_submission",
                        columnList = "submission_id"
                ),
                @Index(
                        name = "idx_test_result_test_case",
                        columnList = "test_case_id"
                ),
                @Index(
                        name = "idx_test_result_submission_status",
                        columnList = "submission_id, status"
                ),
                @Index(
                        name = "idx_test_result_submission_passed",
                        columnList = "submission_id, passed"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionTestCaseResult extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "submission_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_test_result_submission"
            )
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CodeSubmission submission;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "test_case_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_test_result_test_case"
            )
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProblemTestCase testCase;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 40
    )
    private SubmissionStatus status;

    @Column(
            name = "passed",
            nullable = false
    )
    @Builder.Default
    private Boolean passed = false;

    @Lob
    @Column(
            name = "actual_output",
            columnDefinition = "LONGTEXT"
    )
    private String actualOutput;

    @Lob
    @Column(
            name = "expected_output",
            columnDefinition = "LONGTEXT"
    )
    private String expectedOutput;

    @Lob
    @Column(
            name = "standard_error",
            columnDefinition = "LONGTEXT"
    )
    private String standardError;

    @Lob
    @Column(
            name = "compiler_output",
            columnDefinition = "LONGTEXT"
    )
    private String compilerOutput;

    @Column(
            name = "execution_time_seconds"
    )
    private Double executionTimeSeconds;

    @Column(
            name = "memory_used_kilobytes"
    )
    private Long memoryUsedKilobytes;

    @Column(
            name = "score_awarded",
            nullable = false
    )
    @Builder.Default
    private Double scoreAwarded = 0.0;
}