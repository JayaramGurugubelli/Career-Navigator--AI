package career_Navigator_parent.jobtracker.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobTrackerDashboardResponse {

    private long totalApplications;

    private long applied;
    private long underReview;
    private long shortlisted;
    private long interviewScheduled;
    private long interviewCompleted;
    private long offered;
    private long hired;
    private long rejected;
    private long withdrawn;

    private long upcomingInterviews;
    private long pendingOffers;

    private double interviewConversionRate;
    private double offerConversionRate;
    private double selectionRate;
}