package career_Navigator_parent.user.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadProfilePictureRequest {
    MultipartFile file;
}
