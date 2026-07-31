package career_Navigator_parent.analytics.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterDashboardResponse {

    private long totalJobs;
    private long activeJobs;
    private long closedJobs;
    private long draftJobs;

    private long totalApplications;
    private long applicationsUnderReview;
    private long shortlistedApplications;
    private long rejectedApplications;
    private long withdrawnApplications;

    private long scheduledInterviews;
    private long completedInterviews;

    private long offersSent;
    private long offersAccepted;
    private long offersRejected;

    private long candidatesHired;

    private double applicationToInterviewRate;
    private double interviewToOfferRate;
    private double offerAcceptanceRate;
}