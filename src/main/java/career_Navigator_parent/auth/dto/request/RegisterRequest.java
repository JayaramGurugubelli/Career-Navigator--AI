package career_Navigator_parent.auth.dto.request;

import career_Navigator_parent.common.constants.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank
    @Size(max = ValidationConstants.FIRST_NAME_MAX)
    private String firstName;

    @NotBlank
    @Size(max = ValidationConstants.LAST_NAME_MAX)
    private String lastName;

    @NotBlank
    @Size(min = ValidationConstants.USERNAME_MIN, max = ValidationConstants.USERNAME_MAX)
    private String username;

    @Email
    @Size(max = ValidationConstants.EMAIL_MAX)
    private String email;

    @NotBlank
    @Pattern(regexp = "^[6-9][0-9]{9}$")
    private String phoneNumber;

    @NotBlank
    @Size(max = ValidationConstants.PASSWORD_MAX,min = ValidationConstants.PASSWORD_MIN)
    private String password;
}
