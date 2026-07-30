package careerpilot_parent.coding.dto.request;

import careerpilot_parent.coding.enums.ProgrammingLanguage;
import careerpilot_parent.coding.enums.TestCaseGeneratorType;
import careerpilot_parent.coding.enums.TestCaseVisibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Set;

public final class ProblemTestCaseRequests {

    public static final int MAX_BATCH_SIZE = 500;
    public static final int MAX_GENERATED_CASES = 10_000;

    private ProblemTestCaseRequests() {
    }

    public record Create(

            @NotBlank(
                    message = "Test-case input is required."
            )
            String input,

            @NotBlank(
                    message = "Expected output is required."
            )
            String expectedOutput,

            @NotNull(
                    message = "Visibility is required."
            )
            TestCaseVisibility visibility,

            @NotNull(
                    message = "Display order is required."
            )
            @Positive(
                    message = "Display order must be positive."
            )
            Integer displayOrder,

            @NotNull(
                    message = "Score weight is required."
            )
            @Positive(
                    message = "Score weight must be positive."
            )
            Double scoreWeight,

            @Positive(
                    message = "Custom time limit must be positive."
            )
            Double customTimeLimitSeconds,

            @Positive(
                    message = "Custom memory limit must be positive."
            )
            Integer customMemoryLimitMegabytes
    ) {
    }

    public record Update(

            @NotBlank(
                    message = "Test-case input is required."
            )
            String input,

            @NotBlank(
                    message = "Expected output is required."
            )
            String expectedOutput,

            @NotNull(
                    message = "Visibility is required."
            )
            TestCaseVisibility visibility,

            @NotNull(
                    message = "Display order is required."
            )
            @Positive(
                    message = "Display order must be positive."
            )
            Integer displayOrder,

            @NotNull(
                    message = "Score weight is required."
            )
            @Positive(
                    message = "Score weight must be positive."
            )
            Double scoreWeight,

            @Positive(
                    message = "Custom time limit must be positive."
            )
            Double customTimeLimitSeconds,

            @Positive(
                    message = "Custom memory limit must be positive."
            )
            Integer customMemoryLimitMegabytes,

            Boolean active
    ) {
    }

    public record BatchCreate(

            @NotBlank(
                    message = "Batch reference is required."
            )
            @Size(
                    max = 100,
                    message = "Batch reference cannot exceed 100 characters."
            )
            String batchReference,

            @NotEmpty(
                    message = "At least one test case is required."
            )
            @Size(
                    max = MAX_BATCH_SIZE,
                    message = "Maximum 500 test cases are allowed per batch."
            )
            List<@Valid Create> testCases
    ) {
    }

    /*
     * Retained so existing classes importing BulkCreate continue compiling.
     */
    @Deprecated
    public record BulkCreate(

            @NotBlank(
                    message = "Batch reference is required."
            )
            @Size(
                    max = 100,
                    message = "Batch reference cannot exceed 100 characters."
            )
            String batchReference,

            @NotEmpty(
                    message = "At least one test case is required."
            )
            @Size(
                    max = MAX_BATCH_SIZE,
                    message = "Maximum 500 test cases are allowed per batch."
            )
            List<@Valid Create> testCases
    ) {

        public BatchCreate toBatchCreate() {

            return new BatchCreate(
                    batchReference,
                    testCases
            );
        }
    }

    public record Import(

            @NotBlank(
                    message = "Import reference is required."
            )
            @Size(
                    max = 100,
                    message = "Import reference cannot exceed 100 characters."
            )
            String importReference,

            @NotNull(
                    message = "Default visibility is required."
            )
            TestCaseVisibility defaultVisibility,

            @NotNull(
                    message = "Starting display order is required."
            )
            @Positive(
                    message = "Starting display order must be positive."
            )
            Integer startingDisplayOrder,

            @NotNull(
                    message = "Default score weight is required."
            )
            @Positive(
                    message = "Default score weight must be positive."
            )
            Double defaultScoreWeight,

            @NotEmpty(
                    message = "Import rows are required."
            )
            @Size(
                    max = MAX_BATCH_SIZE,
                    message = "Maximum 500 import rows are allowed."
            )
            List<@Valid ImportRow> rows
    ) {
    }

    public record ImportRow(

            @NotBlank(
                    message = "Input is required."
            )
            String input,

            @NotBlank(
                    message = "Expected output is required."
            )
            String expectedOutput,

            TestCaseVisibility visibility,

            @Positive(
                    message = "Score weight must be positive."
            )
            Double scoreWeight,

            @Positive(
                    message = "Custom time limit must be positive."
            )
            Double customTimeLimitSeconds,

            @Positive(
                    message = "Custom memory limit must be positive."
            )
            Integer customMemoryLimitMegabytes
    ) {
    }

    public record Generate(

            @NotNull(
                    message = "Generator type is required."
            )
            TestCaseGeneratorType generatorType,

            @NotNull(
                    message = "Total cases is required."
            )
            @Min(
                    value = 1,
                    message = "At least one test case must be generated."
            )
            @Max(
                    value = MAX_GENERATED_CASES,
                    message = "Maximum 10,000 cases are allowed per generation job."
            )
            Integer totalCases,

            Long randomSeed,

            @NotNull(
                    message = "Minimum array length is required."
            )
            @Min(
                    value = 1,
                    message = "Minimum array length must be at least 1."
            )
            Integer minArrayLength,

            @NotNull(
                    message = "Maximum array length is required."
            )
            @Min(
                    value = 1,
                    message = "Maximum array length must be at least 1."
            )
            Integer maxArrayLength,

            @NotNull(
                    message = "Minimum value is required."
            )
            Long minValue,

            @NotNull(
                    message = "Maximum value is required."
            )
            Long maxValue,

            Boolean includeEdgeCases,

            Boolean includePerformanceCases,

            @NotNull(
                    message = "Visibility is required."
            )
            TestCaseVisibility visibility,

            @NotNull(
                    message = "Batch size is required."
            )
            @Min(
                    value = 10,
                    message = "Batch size must be at least 10."
            )
            @Max(
                    value = MAX_BATCH_SIZE,
                    message = "Batch size cannot exceed 500."
            )
            Integer batchSize,

            @NotNull(
                    message = "Reference language is required."
            )
            ProgrammingLanguage referenceLanguage,

            @NotNull(
                    message = "Reference solution ID is required."
            )
            @Positive(
                    message = "Reference solution ID must be positive."
            )
            Long referenceSolutionId,

            @NotBlank(
                    message = "Input template is required."
            )
            String inputTemplate,

            Long minTargetValue,

            Long maxTargetValue,

            @Positive(
                    message = "Score weight must be positive."
            )
            Double scoreWeight,

            @Positive(
                    message = "Custom time limit must be positive."
            )
            Double customTimeLimitSeconds,

            @Positive(
                    message = "Custom memory limit must be positive."
            )
            Integer customMemoryLimitMegabytes
    ) {
    }

    public record BulkDelete(

            @NotEmpty(
                    message = "At least one test-case ID is required."
            )
            @Size(
                    max = MAX_BATCH_SIZE,
                    message = "Maximum 500 test cases can be deleted per request."
            )
            Set<
                    @NotNull(
                            message = "Test-case ID cannot be null."
                    )
                    @Positive(
                            message = "Test-case ID must be positive."
                    )
                            Long
                    > testCaseIds
    ) {
    }
}