package career_Navigator_parent.admin.dto.response;

import career_Navigator_parent.shared.enums.AccountStatus;
import career_Navigator_parent.shared.enums.RoleName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private AccountStatus accountStatus;
    private boolean emailVerified;
    private Set<RoleName> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
