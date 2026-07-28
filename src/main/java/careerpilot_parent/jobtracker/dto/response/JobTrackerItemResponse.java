package careerpilot_parent.jobtracker.dto.response;

import careerpilot_parent.offer.enums.OfferStatus;
import careerpilot_parent.shared.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobTrackerItemResponse {

    private Long applicationId;

    private Long jobId;
    private String jobTitle;

    private Long companyId;
    private String companyName;
    private String companyLogoUrl;

    private String location;
    private String employmentType;
    private String workMode;

    private ApplicationStatus applicationStatus;

    private LocalDateTime appliedAt;
    private LocalDateTime lastUpdatedAt;

    private LocalDateTime nextInterviewAt;
    private String nextInterviewType;
    private String nextInterviewMode;

    private Long offerId;
    private OfferStatus offerStatus;

    private boolean actionRequired;
    private String actionMessage;
}