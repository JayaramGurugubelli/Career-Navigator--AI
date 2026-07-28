package careerpilot_parent.job.dto.request;

import careerpilot_parent.shared.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicationStatusRequest {

    @NotNull(message = "Application status is required")
    private ApplicationStatus status;

    @Size(
            max = 1000,
            message = "Comment cannot exceed 1000 characters"
    )
    private String comment;
}