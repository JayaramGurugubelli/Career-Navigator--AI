package careerpilot_parent.admin.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterVerificationRequest {

    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;
}
