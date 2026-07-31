package career_Navigator_parent.auth.dto.request;

import career_Navigator_parent.common.constants.ValidationConstants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterRegisterRequest {

    @NotBlank(
            message = "First name is required."
    )
    @Size(
            max = ValidationConstants.FIRST_NAME_MAX,
            message = "First name must not exceed "
                    + ValidationConstants.FIRST_NAME_MAX
                    + " characters."
    )
    private String firstName;

    @NotBlank(
            message = "Last name is required."
    )
    @Size(
            max = ValidationConstants.LAST_NAME_MAX,
            message = "Last name must not exceed "
                    + ValidationConstants.LAST_NAME_MAX
                    + " characters."
    )
    private String lastName;

    @NotBlank(
            message = "Username is required."
    )
    @Size(
            min = ValidationConstants.USERNAME_MIN,
            max = ValidationConstants.USERNAME_MAX,
            message = "Username length is invalid."
    )
    private String username;

    @NotBlank(
            message = "Email is required."
    )
    @Email(
            message = "Enter a valid email address."
    )
    @Size(
            max = ValidationConstants.EMAIL_MAX,
            message = "Email address is too long."
    )
    private String email;

    @NotBlank(
            message = "Password is required."
    )
    @Size(
            min = ValidationConstants.PASSWORD_MIN,
            max = ValidationConstants.PASSWORD_MAX,
            message = "Password length is invalid."
    )
    private String password;

    @Size(
            max = ValidationConstants.PHONE_MAX,
            message = "Phone number is too long."
    )
    private String phoneNumber;
}