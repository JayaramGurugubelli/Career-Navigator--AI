package careerpilot_parent.interview.dto.request;

import careerpilot_parent.interview.enums.InterviewStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInterviewStatusRequest {

    @NotNull(message = "Interview status is required")
    private InterviewStatus status;

    @Size(
            max = 2000,
            message = "Comment must not exceed 2000 characters"
    )
    private String comment;
}