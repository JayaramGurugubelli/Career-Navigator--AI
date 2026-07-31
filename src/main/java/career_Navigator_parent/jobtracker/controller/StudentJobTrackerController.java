package career_Navigator_parent.jobtracker.controller;

import career_Navigator_parent.job.dto.response.ApplicationStatusHistoryResponse;
import career_Navigator_parent.jobtracker.dto.response.*;
import career_Navigator_parent.jobtracker.service.StudentJobTrackerService;
import career_Navigator_parent.offer.enums.OfferStatus;
import career_Navigator_parent.shared.enums.ApplicationStatus;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/student/job-tracker")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('STUDENT')")
public class StudentJobTrackerController {

    private final StudentJobTrackerService studentJobTrackerService;

    @GetMapping("/dashboard")
    public ResponseEntity<JobTrackerDashboardResponse> getDashboard(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate
    ) {
        return ResponseEntity.ok(
                studentJobTrackerService.getDashboard(fromDate, toDate)
        );
    }

    @GetMapping("/applications")
    public ResponseEntity<Page<JobTrackerItemResponse>> getApplications(
            @RequestParam(required = false)
            ApplicationStatus status,
            @RequestParam(required = false)
            String keyword,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @PageableDefault(size = 20, sort = "appliedAt")
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                studentJobTrackerService.getApplications(
                        status,
                        keyword,
                        fromDate,
                        toDate,
                        pageable
                )
        );
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<JobTrackerItemResponse> getApplication(
            @PathVariable
            @Positive(message = "Application id must be positive")
            Long applicationId
    ) {
        return ResponseEntity.ok(
                studentJobTrackerService.getApplication(applicationId)
        );
    }

    @GetMapping("/applications/{applicationId}/history")
    public ResponseEntity<List<ApplicationStatusHistoryResponse>>
    getApplicationHistory(
            @PathVariable
            @Positive(message = "Application id must be positive")
            Long applicationId
    ) {
        return ResponseEntity.ok(
                studentJobTrackerService
                        .getApplicationHistory(applicationId)
        );
    }

    @GetMapping("/applications/{applicationId}/timeline")
    public ResponseEntity<List<ApplicationTimelineResponse>>
    getApplicationTimeline(
            @PathVariable
            @Positive(message = "Application id must be positive")
            Long applicationId
    ) {
        return ResponseEntity.ok(
                studentJobTrackerService
                        .getApplicationTimeline(applicationId)
        );
    }

    @GetMapping("/upcoming-interviews")
    public ResponseEntity<List<UpcomingInterviewResponse>>
    getUpcomingInterviews() {
        return ResponseEntity.ok(
                studentJobTrackerService.getUpcomingInterviews()
        );
    }

    @GetMapping("/offers")
    public ResponseEntity<List<OfferSummaryResponse>> getOffers(
            @RequestParam(required = false)
            OfferStatus status
    ) {
        return ResponseEntity.ok(
                studentJobTrackerService.getOffers(status)
        );
    }
}
