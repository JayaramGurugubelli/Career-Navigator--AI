package career_Navigator_parent.auth.dto.response;
import career_Navigator_parent.shared.enums.AccountStatus;
import career_Navigator_parent.user.dto.response.UserResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long userId;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Long expiresIn;

    private AccountStatus accountStatus;

    private UserResponse user;
}
