package careerpilot_parent.coding.dto.request;

import careerpilot_parent.coding.enums.ProgrammingLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class ProblemStarterCodeRequests {

    private ProblemStarterCodeRequests() {
    }

    public record Create(

            @NotNull(
                    message = "Programming language is required."
            )
            ProgrammingLanguage language,

            @NotBlank(
                    message = "Starter code is required."
            )
            String starterCode,

            String driverCode,

            @Size(
                    max = 500,
                    message = "Method signature cannot exceed 500 characters."
            )
            String methodSignature

    ) {
    }

    public record Update(

            @NotBlank(
                    message = "Starter code is required."
            )
            String starterCode,

            String driverCode,

            @Size(
                    max = 500,
                    message = "Method signature cannot exceed 500 characters."
            )
            String methodSignature,

            Boolean active

    ) {
    }
}