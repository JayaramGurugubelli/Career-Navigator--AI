package career_Navigator_parent.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSocialLinksRequest {
    @Size(max = 255)
    private String linkedInUrl;

    @Size(max = 255)
    private String githubUrl;

    @Size(max = 255)
    private String portfolioUrl;

    @Size(max = 255)
    private String leetCodeUrl;

    @Size(max = 255)
    private String codeforcesUrl;

    @Size(max = 255)
    private String hackerRankUrl;

    @Size(max = 255)
    private String codeChefUrl;
}
