package careerpilot_parent.jobtracker.projection;

import java.time.LocalDateTime;

public interface StudentApplicationTrackerProjection {

    Long getApplicationId();

    Long getJobId();

    String getJobTitle();

    Long getCompanyId();

    String getCompanyName();

    String getCompanyLogoUrl();

    String getLocation();

    String getEmploymentType();

    String getWorkMode();

    String getApplicationStatus();

    LocalDateTime getAppliedAt();

    LocalDateTime getLastUpdatedAt();

    LocalDateTime getNextInterviewAt();

    String getNextInterviewType();

    String getNextInterviewMode();

    Long getOfferId();

    String getOfferStatus();
}