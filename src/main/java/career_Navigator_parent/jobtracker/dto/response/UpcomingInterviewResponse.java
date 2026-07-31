package career_Navigator_parent.jobtracker.dto.response;

import career_Navigator_parent.interview.enums.InterviewMode;
import career_Navigator_parent.interview.enums.InterviewStatus;
import career_Navigator_parent.interview.enums.InterviewType;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingInterviewResponse {

    private Long interviewId;

    private Long applicationId;

    private Long jobId;
    private String jobTitle;

    private Long companyId;
    private String companyName;
    private String companyLogoUrl;

    private InterviewType interviewType;
    private InterviewMode interviewMode;
    private InterviewStatus interviewStatus;

    private LocalDateTime scheduledAt;

    private Integer durationMinutes;

    private String meetingLink;
    private String location;

    private String instructions;

    private boolean today;
    private boolean actionRequired;
}