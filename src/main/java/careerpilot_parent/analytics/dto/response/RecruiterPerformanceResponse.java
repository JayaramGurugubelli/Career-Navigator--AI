package careerpilot_parent.analytics.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterPerformanceResponse {

    private Long recruiterId;
    private String recruiterName;
    private String companyName;

    private long totalJobs;
    private long totalApplications;
    private long totalInterviews;
    private long totalOffers;
    private long totalHires;

    private double applicationToInterviewRate;
    private double interviewToOfferRate;
    private double offerAcceptanceRate;
    private double applicationToHireRate;

    private double averageApplicationsPerJob;
    private double averageHiresPerJob;
}