package careerpilot_parent.savedjob.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedJobResponse {

    private Long savedJobId;

    private Long jobId;
    private String jobTitle;
    private String jobSlug;

    private Long companyId;
    private String companyName;
    private String companyLogoUrl;

    private String location;
    private String employmentType;
    private String workMode;
    private String experienceLevel;

    private Integer minimumExperience;
    private Integer maximumExperience;

    private LocalDate applicationDeadline;

    private String jobStatus;

    private LocalDateTime publishedAt;
    private LocalDateTime savedAt;

    private boolean applied;
}