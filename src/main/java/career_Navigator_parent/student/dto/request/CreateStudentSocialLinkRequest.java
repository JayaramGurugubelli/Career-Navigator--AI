package career_Navigator_parent.student.dto.request;

import career_Navigator_parent.student.enums.SocialPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStudentSocialLinkRequest {

    @NotNull(message = "Platform is required.")
    private SocialPlatform platform;

    @NotBlank(message = "URL is required.")
    @Size(max = 500, message = "URL must not exceed 500 characters.")
    @Pattern(
            regexp = "^(https?://).+$",
            message = "URL must start with http:// or https://"
    )
    private String url;

    @NotNull(message = "Display order is required.")
    @Positive(message = "Display order must be greater than zero.")
    private Integer displayOrder;

    @NotNull(message = "Visibility is required.")
    private Boolean visible;

}