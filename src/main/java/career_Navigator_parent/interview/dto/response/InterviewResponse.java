package career_Navigator_parent.interview.dto.response;

import career_Navigator_parent.interview.enums.InterviewMode;
import career_Navigator_parent.interview.enums.InterviewResult;
import career_Navigator_parent.interview.enums.InterviewStatus;
import career_Navigator_parent.interview.enums.InterviewType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewResponse {

    private Long id;

    private Long applicationId;

    private Long jobId;

    private String jobTitle;

    private Long companyId;

    private String companyName;

    private Long studentId;

    private String studentName;

    private Long recruiterId;

    private String recruiterName;

    private String title;

    private String description;

    private InterviewType interviewType;

    private InterviewMode interviewMode;

    private Integer roundNumber;

    private LocalDateTime scheduledAt;

    private LocalDateTime endAt;

    private Integer durationMinutes;

    private String meetingUrl;

    private String meetingId;

    private String meetingPassword;

    private String location;

    private String interviewerName;

    private String interviewerEmail;

    private String interviewerDesignation;

    private String instructions;

    private InterviewStatus status;

    private InterviewResult result;

    private String studentResponseNotes;

    private String feedback;

    private String strengths;

    private String areasForImprovement;

    private Double technicalScore;

    private Double communicationScore;

    private Double problemSolvingScore;

    private Double overallScore;

    private LocalDateTime confirmedAt;

    private LocalDateTime declinedAt;

    private LocalDateTime completedAt;

    private LocalDateTime cancelledAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}