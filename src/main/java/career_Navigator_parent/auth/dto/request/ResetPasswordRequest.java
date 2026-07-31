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
public class ResetPasswordRequest {


    @NotBlank(message = "Reset token is required")
    private String resetToken;



    @NotBlank(message = "New password is required")
    @Size(
            min = ValidationConstants.PASSWORD_MIN,
            max = ValidationConstants.PASSWORD_MAX,
            message = "Password length is invalid"
    )
    private String newPassword;



    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;


}