package career_Navigator_parent.user.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileSummaryResponse {
    private Long userId;

    private String fullName;

    private String headline;

    private String city;

    private String country;

    private String profilePicture;

    private boolean emailVerified;

    private boolean mobileVerified;

}
