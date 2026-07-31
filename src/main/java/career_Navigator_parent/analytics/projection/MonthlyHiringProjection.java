package career_Navigator_parent.analytics.projection;

public interface MonthlyHiringProjection {

    String getPeriod();

    Long getApplications();

    Long getInterviews();

    Long getOffers();

    Long getHires();
}