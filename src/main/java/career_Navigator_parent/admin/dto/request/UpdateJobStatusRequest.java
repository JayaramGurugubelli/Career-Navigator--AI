package career_Navigator_parent.admin.dto.request;

import career_Navigator_parent.company.enums.JobStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateJobStatusRequest {

    @NotNull(message = "Job status is required")
    private JobStatus status;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
}
