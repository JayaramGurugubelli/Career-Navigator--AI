package career_Navigator_parent.user.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteProfilePictureRequest {
    private Long userId;
}
