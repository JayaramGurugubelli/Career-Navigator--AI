package career_Navigator_parent.resume.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateResumeSectionRequest {

    @NotNull
    @Positive
    private Integer displayOrder;

    @NotNull
    private Boolean visible;

    @Size(max = 100)
    private String sectionHeading;

    private Boolean highlight;

    @Size(max = 3000)
    private String customDescription;

}