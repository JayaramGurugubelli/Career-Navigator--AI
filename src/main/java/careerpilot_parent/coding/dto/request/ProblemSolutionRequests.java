package careerpilot_parent.coding.dto.request;

import careerpilot_parent.coding.enums.ProgrammingLanguage;
import careerpilot_parent.coding.enums.SolutionApproach;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class ProblemSolutionRequests {

    private ProblemSolutionRequests() {
    }

    public record Create(

            @NotNull(message = "Programming language is required.")
            ProgrammingLanguage language,

            @NotNull(message = "Solution approach is required.")
            SolutionApproach approach,

            @NotBlank(message = "Solution title is required.")
            @Size(
                    max = 200,
                    message = "Solution title cannot exceed 200 characters."
            )
            String title,

            @NotBlank(message = "Solution explanation is required.")
            String explanation,

            @NotBlank(message = "Solution source code is required.")
            String sourceCode,

            @NotBlank(message = "Time complexity is required.")
            @Size(
                    max = 100,
                    message = "Time complexity cannot exceed 100 characters."
            )
            String timeComplexity,

            @NotBlank(message = "Space complexity is required.")
            @Size(
                    max = 100,
                    message = "Space complexity cannot exceed 100 characters."
            )
            String spaceComplexity,

            Boolean official,

            Boolean active
    ) {
    }

    public record Update(

            @NotNull(message = "Programming language is required.")
            ProgrammingLanguage language,

            @NotNull(message = "Solution approach is required.")
            SolutionApproach approach,

            @NotBlank(message = "Solution title is required.")
            @Size(max = 200)
            String title,

            @NotBlank(message = "Solution explanation is required.")
            String explanation,

            @NotBlank(message = "Solution source code is required.")
            String sourceCode,

            @NotBlank(message = "Time complexity is required.")
            @Size(max = 100)
            String timeComplexity,

            @NotBlank(message = "Space complexity is required.")
            @Size(max = 100)
            String spaceComplexity,

            Boolean official,

            Boolean active
    ) {
    }

    public record EditorialUpsert(

            @NotNull(message = "Editorial language is required.")
            ProgrammingLanguage language,

            @NotNull(message = "Editorial approach is required.")
            SolutionApproach approach,

            @NotBlank(message = "Editorial title is required.")
            @Size(max = 200)
            String title,

            @NotBlank(message = "Editorial explanation is required.")
            String explanation,

            @NotBlank(message = "Editorial source code is required.")
            String sourceCode,

            @NotBlank(message = "Time complexity is required.")
            @Size(max = 100)
            String timeComplexity,

            @NotBlank(message = "Space complexity is required.")
            @Size(max = 100)
            String spaceComplexity
    ) {
    }
}