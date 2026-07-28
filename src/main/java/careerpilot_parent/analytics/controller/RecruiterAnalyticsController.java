package careerpilot_parent.analytics.controller;

import careerpilot_parent.analytics.dto.response.*;
import careerpilot_parent.analytics.service.RecruiterAnalyticsService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/recruiter/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterAnalyticsController {

    private final RecruiterAnalyticsService
            recruiterAnalyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<RecruiterDashboardResponse>
    getDashboard(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate
    ) {

        return ResponseEntity.ok(
                recruiterAnalyticsService.getDashboard(
                        fromDate,
                        toDate
                )
        );
    }

    @GetMapping("/jobs")
    public ResponseEntity<Page<JobAnalyticsResponse>>
    getJobAnalytics(
            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @PageableDefault(
                    size = 20,
                    sort = "createdAt"
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                recruiterAnalyticsService.getJobAnalytics(
                        keyword,
                        fromDate,
                        toDate,
                        pageable
                )
        );
    }

    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<JobAnalyticsResponse>
    getJobAnalyticsById(
            @PathVariable Long jobId
    ) {

        return ResponseEntity.ok(
                recruiterAnalyticsService
                        .getJobAnalyticsById(jobId)
        );
    }

    @GetMapping("/application-funnel")
    public ResponseEntity<ApplicationFunnelResponse>
    getApplicationFunnel(
            @RequestParam(required = false)
            Long jobId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate
    ) {

        return ResponseEntity.ok(
                recruiterAnalyticsService
                        .getApplicationFunnel(
                                jobId,
                                fromDate,
                                toDate
                        )
        );
    }

    @GetMapping("/hiring-trends")
    public ResponseEntity<List<HiringTrendResponse>>
    getHiringTrends(
            @RequestParam(defaultValue = "6")
            int months
    ) {

        return ResponseEntity.ok(
                recruiterAnalyticsService
                        .getHiringTrends(months)
        );
    }

    @GetMapping("/sources")
    public ResponseEntity<List<SourceAnalyticsResponse>>
    getSourceAnalytics(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate
    ) {

        return ResponseEntity.ok(
                recruiterAnalyticsService
                        .getSourceAnalytics(
                                fromDate,
                                toDate
                        )
        );
    }

    @GetMapping("/performance")
    public ResponseEntity<RecruiterPerformanceResponse>
    getRecruiterPerformance(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate
    ) {

        return ResponseEntity.ok(
                recruiterAnalyticsService
                        .getRecruiterPerformance(
                                fromDate,
                                toDate
                        )
        );
    }
}