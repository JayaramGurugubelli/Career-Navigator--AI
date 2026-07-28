package careerpilot_parent.analytics.projection;

public interface RecruiterDashboardProjection {

    Long getTotalJobs();

    Long getActiveJobs();

    Long getClosedJobs();

    Long getDraftJobs();

    Long getTotalApplications();

    Long getScheduledInterviews();

    Long getCompletedInterviews();

    Long getOffersSent();

    Long getOffersAccepted();

    Long getOffersRejected();

    Long getCandidatesHired();
}