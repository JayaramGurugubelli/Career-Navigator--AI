package careerpilot_parent.coding.dto.request;

import careerpilot_parent.coding.enums.ProblemDifficulty;
import careerpilot_parent.coding.enums.ProgrammingLanguage;
import careerpilot_parent.coding.enums.TestCaseVisibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public final class ProblemImportRequests {

    private ProblemImportRequests() {
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportProblems {

        @NotBlank(message = "Import reference is required.")
        @Size(
                max = 150,
                message = "Import reference must not exceed 150 characters."
        )
        private String importReference;

        @Builder.Default
        private Boolean continueOnError = Boolean.FALSE;

        @NotEmpty(message = "At least one problem is required.")
        @Size(
                max = 500,
                message = "A single import cannot contain more than 500 problems."
        )
        @Valid
        @Builder.Default
        private List<ImportProblem> problems = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportProblem {

        @NotBlank(message = "Problem title is required.")
        @Size(max = 200)
        private String title;

        @Size(max = 220)
        private String slug;

        @NotBlank(message = "Problem description is required.")
        @Size(max = 30000)
        private String description;

        @NotBlank(message = "Input format is required.")
        @Size(max = 10000)
        private String inputFormat;

        @NotBlank(message = "Output format is required.")
        @Size(max = 10000)
        private String outputFormat;

        @NotBlank(message = "Constraints are required.")
        @Size(max = 10000)
        private String constraints;

        @Size(max = 30000)
        private String explanation;

        @NotNull(message = "Difficulty is required.")
        private ProblemDifficulty difficulty;

        @NotNull(message = "Time limit is required.")
        @Min(value = 100, message = "Time limit must be at least 100 milliseconds.")
        @Max(value = 30000, message = "Time limit cannot exceed 30000 milliseconds.")
        private Integer timeLimitMilliseconds;

        @NotNull(message = "Memory limit is required.")
        @Min(value = 16, message = "Memory limit must be at least 16 MB.")
        @Max(value = 2048, message = "Memory limit cannot exceed 2048 MB.")
        private Integer memoryLimitMegabytes;

        @NotNull(message = "Function-based flag is required.")
        private Boolean functionBased;

        @Size(max = 150)
        private String functionName;

        @Size(max = 500)
        private String expectedComplexity;

        @Builder.Default
        private Boolean premium = Boolean.FALSE;

        @NotEmpty(message = "At least one tag ID is required.")
        @Size(max = 25)
        @Builder.Default
        private List<@NotNull @Positive Long> tagIds = new ArrayList<>();

        @NotEmpty(message = "At least one test case is required.")
        @Size(max = 500)
        @Valid
        @Builder.Default
        private List<ImportTestCase> testCases = new ArrayList<>();

        @NotEmpty(message = "At least one starter code is required.")
        @Size(max = 20)
        @Valid
        @Builder.Default
        private List<ImportStarterCode> starterCodes = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportTestCase {

        @NotBlank(message = "Test-case input is required.")
        private String input;

        @NotBlank(message = "Expected output is required.")
        private String expectedOutput;

        @NotNull(message = "Test-case visibility is required.")
        private TestCaseVisibility visibility;

        @NotNull(message = "Display order is required.")
        @Min(value = 1, message = "Display order must be at least 1.")
        private Integer displayOrder;

        @NotNull(message = "Score weight is required.")
        @Min(value = 0)
        @Max(value = 100)
        private Integer scoreWeight;

        @DecimalMin(value = "0.1")
        @DecimalMax(value = "30.0")
        private Double customTimeLimitSeconds;

        @Min(16)
        @Max(2048)
        private Integer customMemoryLimitMegabytes;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportStarterCode {

        @NotNull(message = "Programming language is required.")
        private ProgrammingLanguage language;

        @NotBlank(message = "Starter code is required.")
        private String starterCode;

        private String driverCode;

        @Size(max = 2000)
        private String methodSignature;
    }
}