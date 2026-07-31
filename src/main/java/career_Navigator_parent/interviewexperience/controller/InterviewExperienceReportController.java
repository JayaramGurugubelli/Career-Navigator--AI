package career_Navigator_parent.interviewexperience.controller;

import career_Navigator_parent.interviewexperience.dto.request.CreateInterviewExperienceReportRequest;
import career_Navigator_parent.interviewexperience.dto.response.InterviewExperienceReportResponse;
import career_Navigator_parent.interviewexperience.service.InterviewExperienceReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        "/api/interview-experiences/{experienceId}/reports"
)
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class InterviewExperienceReportController {

    private final InterviewExperienceReportService reportService;

    @PostMapping
    public ResponseEntity<InterviewExperienceReportResponse>
    reportExperience(
            @PathVariable
            Long experienceId,

            @Valid
            @RequestBody
            CreateInterviewExperienceReportRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        reportService.reportExperience(
                                experienceId,
                                request
                        )
                );
    }
}