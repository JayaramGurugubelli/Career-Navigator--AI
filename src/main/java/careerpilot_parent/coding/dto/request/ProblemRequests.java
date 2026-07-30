package careerpilot_parent.coding.dto.request;

import careerpilot_parent.coding.enums.ProblemDifficulty;
import careerpilot_parent.coding.enums.ProblemStatus;
import careerpilot_parent.coding.enums.ProgrammingLanguage;
import careerpilot_parent.coding.enums.TestCaseVisibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public final class ProblemRequests {

    private ProblemRequests() {
    }

    public record TestCase(

            @NotBlank(message = "Test case input is required")
            String input,

            @NotBlank(message = "Expected output is required")
            String expectedOutput,

            @NotNull(message = "Test case visibility is required")
            TestCaseVisibility visibility,

            @NotNull(message = "Display order is required")
            @Min(
                    value = 0,
                    message = "Display order cannot be negative"
            )
            Integer displayOrder,

            @NotNull(message = "Score weight is required")
            @Min(
                    value = 1,
                    message = "Score weight must be at least 1"
            )
            @Max(
                    value = 100,
                    message = "Score weight cannot exceed 100"
            )
            Integer scoreWeight,

            @Positive(
                    message = "Custom time limit must be greater than zero"
            )
            Double customTimeLimitSeconds,

            @Positive(
                    message = "Custom memory limit must be greater than zero"
            )
            Integer customMemoryLimitMegabytes
    ) {
    }

    public record Starter(

            @NotNull(message = "Programming language is required")
            ProgrammingLanguage language,

            @NotBlank(message = "Starter code is required")
            String starterCode,

            String driverCode,

            String methodSignature
    ) {
    }

    public record Create(

            @NotBlank(message = "Problem title is required")
            @Size(
                    max = 200,
                    message = "Problem title cannot exceed 200 characters"
            )
            String title,

            @NotBlank(message = "Problem description is required")
            String description,

            String inputFormat,

            String outputFormat,

            String constraints,

            String explanation,

            @NotNull(message = "Problem difficulty is required")
            ProblemDifficulty difficulty,

            @NotNull(message = "Time limit is required")
            @Min(
                    value = 100,
                    message = "Time limit must be at least 100 milliseconds"
            )
            Integer timeLimitMilliseconds,

            @NotNull(message = "Memory limit is required")
            @Min(
                    value = 16,
                    message = "Memory limit must be at least 16 MB"
            )
            Integer memoryLimitMegabytes,

            Boolean functionBased,

            String functionName,

            String expectedComplexity,

            Boolean premium,

            Set<@NotNull @Positive Long> tagIds,

            @NotEmpty(message = "At least one test case is required")
            List<@Valid TestCase> testCases,

            @NotEmpty(message = "At least one starter code is required")
            List<@Valid Starter> starterCodes
    ) {
    }

    public record Update(

            @NotBlank(message = "Problem title is required")
            @Size(
                    max = 200,
                    message = "Problem title cannot exceed 200 characters"
            )
            String title,

            @NotBlank(message = "Problem description is required")
            String description,

            String inputFormat,

            String outputFormat,

            String constraints,

            String explanation,

            @NotNull(message = "Problem difficulty is required")
            ProblemDifficulty difficulty,

            @NotNull(message = "Time limit is required")
            @Min(
                    value = 100,
                    message = "Time limit must be at least 100 milliseconds"
            )
            Integer timeLimitMilliseconds,

            @NotNull(message = "Memory limit is required")
            @Min(
                    value = 16,
                    message = "Memory limit must be at least 16 MB"
            )
            Integer memoryLimitMegabytes,

            Boolean functionBased,

            String functionName,

            String expectedComplexity,

            Boolean premium,

            Boolean active,

            Set<@NotNull @Positive Long> tagIds,

            @NotEmpty(message = "At least one test case is required")
            List<@Valid TestCase> testCases,

            @NotEmpty(message = "At least one starter code is required")
            List<@Valid Starter> starterCodes
    ) {
    }

    public record Status(

            @NotNull(message = "Problem status is required")
            ProblemStatus status
    ) {
    }
    public record Activation(
            @jakarta.validation.constraints.NotNull(
                    message = "Active status is required."
            )
            Boolean active
    ) {
    }
}