package career_Navigator_parent.auth.dto.response;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private Long userId;

    private String username;

    private String email;

    private Boolean emailVerificationRequired;

    private String message;
}
