package career_Navigator_parent.resume.dto.response;

import career_Navigator_parent.resume.enums.ResumeSectionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeSectionResponse {

    private Long id;

    private Long resumeId;

    private ResumeSectionType sectionType;

    private Long referenceId;

    private Integer displayOrder;

    private Boolean visible;

    private String sectionHeading;

    private Boolean highlight;

    private String customDescription;

}