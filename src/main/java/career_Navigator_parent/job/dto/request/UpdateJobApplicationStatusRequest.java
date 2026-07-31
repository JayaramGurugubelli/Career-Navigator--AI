package career_Navigator_parent.job.dto.request;

import career_Navigator_parent.shared.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateJobApplicationStatusRequest {

    @NotNull(message = "Job application status is required")
    private ApplicationStatus status;

    @Size(
            max = 2000,
            message = "Comment must not exceed 2000 characters"
    )
    private String comment;
}