package careerpilot_parent.coding.dto.response;

import java.time.LocalDateTime;

public record ProblemTagResponse(

        Long id,
        String name,
        String slug,
        String description,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}