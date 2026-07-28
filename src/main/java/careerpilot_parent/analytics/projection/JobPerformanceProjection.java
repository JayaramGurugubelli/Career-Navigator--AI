package careerpilot_parent.analytics.projection;

import java.time.LocalDateTime;

public interface JobPerformanceProjection {

    Long getJobId();

    String getJobTitle();

    String getStatus();

    Long getTotalApplications();

    Long getInterviewCount();

    Long getOfferCount();

    Long getHireCount();

    LocalDateTime getPublishedAt();

    LocalDateTime getClosedAt();
}