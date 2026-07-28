package careerpilot_parent.job.controller;

import careerpilot_parent.job.dto.request.UpdateJobApplicationStatusRequest;
import careerpilot_parent.job.dto.request.UpdateRecruiterNotesRequest;
import careerpilot_parent.job.dto.response.ApplicationStatusHistoryResponse;
import careerpilot_parent.job.dto.response.JobApplicationResponse;
import careerpilot_parent.job.service.ApplicationStatusHistoryService;
import careerpilot_parent.recruiter.service.RecruiterJobApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter/applications")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterApplicationController {

    private final RecruiterJobApplicationService
            recruiterJobApplicationService;
    private final ApplicationStatusHistoryService
            applicationStatusHistoryService;

    @GetMapping("/{applicationId}")
    public ResponseEntity<JobApplicationResponse> getApplicationById(
            @PathVariable
            @Positive(message = "Application id must be positive")
            Long applicationId
    ) {
        return ResponseEntity.ok(
                recruiterJobApplicationService
                        .getApplicationById(applicationId)
        );
    }

    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<JobApplicationResponse>
    updateApplicationStatus(
            @PathVariable
            @Positive(message = "Application id must be positive")
            Long applicationId,
            @Valid @RequestBody
            UpdateJobApplicationStatusRequest request
    ) {
        return ResponseEntity.ok(
                recruiterJobApplicationService
                        .updateApplicationStatus(
                                applicationId,
                                request
                        )
        );
    }

    @PatchMapping("/{applicationId}/notes")
    public ResponseEntity<JobApplicationResponse>
    updateRecruiterNotes(
            @PathVariable
            @Positive(message = "Application id must be positive")
            Long applicationId,
            @Valid @RequestBody
            UpdateRecruiterNotesRequest request
    ) {
        return ResponseEntity.ok(
                recruiterJobApplicationService
                        .updateRecruiterNotes(
                                applicationId,
                                request
                        )
        );
    }

    @GetMapping("/{applicationId}/history")
    public ResponseEntity<List<ApplicationStatusHistoryResponse>>
    getApplicationHistory(
            @PathVariable
            @Positive(message = "Application id must be positive")
            Long applicationId
    ) {
        return ResponseEntity.ok(
                applicationStatusHistoryService
                        .getRecruiterApplicationHistory(applicationId)
        );
    }
}
