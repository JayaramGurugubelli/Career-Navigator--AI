package career_Navigator_parent.coding.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class ProblemTagRequests {

    private ProblemTagRequests() {
    }

    public record Create(

            @NotBlank(message = "Tag name is required")
            @Size(
                    max = 80,
                    message = "Tag name cannot exceed 80 characters"
            )
            String name,

            @Size(
                    max = 300,
                    message = "Tag description cannot exceed 300 characters"
            )
            String description
    ) {
    }

    public record Update(

            @NotBlank(message = "Tag name is required")
            @Size(
                    max = 80,
                    message = "Tag name cannot exceed 80 characters"
            )
            String name,

            @Size(
                    max = 300,
                    message = "Tag description cannot exceed 300 characters"
            )
            String description,

            Boolean active
    ) {
    }
}