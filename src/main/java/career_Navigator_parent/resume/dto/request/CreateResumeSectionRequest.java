package career_Navigator_parent.resume.dto.request;

import career_Navigator_parent.resume.enums.ResumeSectionType;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateResumeSectionRequest {

    @NotNull
    private ResumeSectionType sectionType;

    @NotNull
    @Positive
    private Long referenceId;

    @NotNull
    @Positive
    private Integer displayOrder;

    @NotNull
    private Boolean visible;

    @Size(max = 100)
    private String sectionHeading;

    @Builder.Default
    private Boolean highlight = false;

    @Size(max = 3000)
    private String customDescription;

}