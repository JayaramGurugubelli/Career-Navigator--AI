package careerpilot_parent.analytics.dto.response;

import careerpilot_parent.company.enums.JobStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobAnalyticsResponse {

    private Long jobId;
    private String jobTitle;
    private JobStatus status;

    private long totalApplications;
    private long applicationsUnderReview;
    private long shortlistedApplications;
    private long rejectedApplications;
    private long withdrawnApplications;

    private long scheduledInterviews;
    private long completedInterviews;

    private long offersSent;
    private long offersAccepted;
    private long candidatesHired;

    private double applicationToInterviewRate;
    private double interviewToOfferRate;
    private double offerAcceptanceRate;
    private double applicationToHireRate;

    private LocalDateTime publishedAt;
    private LocalDateTime closedAt;
}