package careerpilot_parent.job.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRecruiterNotesRequest {

    @Size(
            max = 10000,
            message = "Recruiter notes must not exceed 10000 characters"
    )
    private String notes;
}