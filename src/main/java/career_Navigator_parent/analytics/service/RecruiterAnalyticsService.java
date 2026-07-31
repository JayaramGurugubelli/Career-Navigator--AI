package career_Navigator_parent.analytics.service;

import career_Navigator_parent.analytics.dto.response.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface RecruiterAnalyticsService {

    RecruiterDashboardResponse getDashboard(
            LocalDate fromDate,
            LocalDate toDate
    );

    Page<JobAnalyticsResponse> getJobAnalytics(
            String keyword,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    );

    JobAnalyticsResponse getJobAnalyticsById(
            Long jobId
    );

    ApplicationFunnelResponse getApplicationFunnel(
            Long jobId,
            LocalDate fromDate,
            LocalDate toDate
    );

    List<HiringTrendResponse> getHiringTrends(
            int months
    );

    List<SourceAnalyticsResponse> getSourceAnalytics(
            LocalDate fromDate,
            LocalDate toDate
    );

    RecruiterPerformanceResponse getRecruiterPerformance(
            LocalDate fromDate,
            LocalDate toDate
    );
}