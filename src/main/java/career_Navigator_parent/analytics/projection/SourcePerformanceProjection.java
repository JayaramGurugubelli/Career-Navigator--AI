package career_Navigator_parent.analytics.projection;

public interface SourcePerformanceProjection {

    String getSource();

    Long getApplicationCount();

    Long getInterviewCount();

    Long getOfferCount();

    Long getHireCount();
}