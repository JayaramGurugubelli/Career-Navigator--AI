package career_Navigator_parent.admin.dto.request;

import career_Navigator_parent.shared.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserStatusRequest {

    @NotNull(message = "Account status is required")
    private AccountStatus status;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
}
