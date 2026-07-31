package career_Navigator_parent.interviewexperience.dto.response;

import career_Navigator_parent.interviewexperience.enums.InterviewExperienceReportReason;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewExperienceReportResponse {

    private Long id;

    private Long interviewExperienceId;

    private String companyName;

    private InterviewExperienceReportReason reason;

    private String additionalDetails;

    private Boolean reviewed;

    private Boolean resolved;

    /*
     * Admin APIs only.
     */
    private String reportedByDisplayName;

    private String reviewedByDisplayName;

    private String adminNotes;

    private LocalDateTime createdAt;

    private LocalDateTime reviewedAt;
}