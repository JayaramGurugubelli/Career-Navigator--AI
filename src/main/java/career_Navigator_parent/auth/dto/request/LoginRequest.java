package career_Navigator_parent.auth.dto.request;
import career_Navigator_parent.common.constants.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    @Size(max = ValidationConstants.PASSWORD_MAX,min = ValidationConstants.PASSWORD_MIN)
    private String password;

}
