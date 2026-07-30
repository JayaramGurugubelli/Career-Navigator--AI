package careerpilot_parent.coding.dto.request;

import careerpilot_parent.coding.enums.ProgrammingLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SubmitCodeRequest(

        @NotNull(message = "Problem ID is required")
        Long problemId,

        @NotNull(message = "Programming language is required")
        ProgrammingLanguage programmingLanguage,

        @NotBlank(message = "Source code is required")
        @Size(
                max = 100_000,
                message = "Source code cannot exceed 100000 characters"
        )
        String sourceCode

) {
}