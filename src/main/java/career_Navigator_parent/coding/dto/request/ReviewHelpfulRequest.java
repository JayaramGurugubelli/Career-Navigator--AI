package career_Navigator_parent.coding.dto.request;

import jakarta.validation.constraints.NotNull;

public record ReviewHelpfulRequest(

        @NotNull(message = "Helpful status is required.")
        Boolean helpful

) {
}