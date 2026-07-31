package career_Navigator_parent.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeactivateAccountRequest {
    @NotBlank
    private String password;
}
