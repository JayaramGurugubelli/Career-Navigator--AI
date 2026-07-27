package careerpilot_parent.job.controller;

import careerpilot_parent.company.dto.request.CreateJobPostingRequest;
import careerpilot_parent.company.dto.request.UpdateJobPostingRequest;
import careerpilot_parent.company.dto.response.JobPostingResponse;
import careerpilot_parent.company.enums.ApplicationStatus;
import careerpilot_parent.company.enums.JobStatus;
import careerpilot_parent.job.dto.response.JobApplicationResponse;
import careerpilot_parent.recruiter.service.RecruiterJobApplicationService;
import careerpilot_parent.recruiter.service.RecruiterJobApplicationService;
import careerpilot_parent.recruiter.service.RecruiterJobService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/recruiter/jobs")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterJobController {

    private final RecruiterJobService recruiterJobService;

    private final careerpilot_parent.recruiter.service.RecruiterJobApplicationService
            recruiterJobApplicationService;

    /*
     * =========================================================
     * JOB POSTING ENDPOINTS
     * =========================================================
     */

    @PostMapping
    public ResponseEntity<JobPostingResponse> createJob(
            @jakarta.validation.Valid
            @RequestBody
            CreateJobPostingRequest request
    ) {

        JobPostingResponse response =
                recruiterJobService.createJob(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<Page<JobPostingResponse>> getMyJobs(
            @RequestParam(required = false)
            JobStatus status,

            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = DESC
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                recruiterJobService.getMyJobs(
                        status,
                        pageable
                )
        );
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobPostingResponse> getMyJobById(
            @PathVariable
            @Positive(message = "Job id must be positive")
            Long jobId
    ) {

        return ResponseEntity.ok(
                recruiterJobService.getMyJobById(jobId)
        );
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<JobPostingResponse> updateJob(
            @PathVariable
            @Positive(message = "Job id must be positive")
            Long jobId,

            @jakarta.validation.Valid
            @RequestBody
            UpdateJobPostingRequest request
    ) {

        return ResponseEntity.ok(
                recruiterJobService.updateJob(
                        jobId,
                        request
                )
        );
    }

    @PatchMapping("/{jobId}/publish")
    public ResponseEntity<JobPostingResponse> publishJob(
            @PathVariable
            @Positive(message = "Job id must be positive")
            Long jobId
    ) {

        return ResponseEntity.ok(
                recruiterJobService.publishJob(jobId)
        );
    }

    @PatchMapping("/{jobId}/pause")
    public ResponseEntity<JobPostingResponse> pauseJob(
            @PathVariable
            @Positive(message = "Job id must be positive")
            Long jobId
    ) {

        return ResponseEntity.ok(
                recruiterJobService.pauseJob(jobId)
        );
    }

    @PatchMapping("/{jobId}/close")
    public ResponseEntity<JobPostingResponse> closeJob(
            @PathVariable
            @Positive(message = "Job id must be positive")
            Long jobId
    ) {

        return ResponseEntity.ok(
                recruiterJobService.closeJob(jobId)
        );
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> archiveJob(
            @PathVariable
            @Positive(message = "Job id must be positive")
            Long jobId
    ) {

        recruiterJobService.archiveJob(jobId);

        return ResponseEntity
                .noContent()
                .build();
    }

    /*
     * =========================================================
     * JOB APPLICATION ENDPOINTS
     * =========================================================
     */

    @GetMapping("/{jobId}/applications")
    public ResponseEntity<Page<JobApplicationResponse>>
    getApplicationsForJob(
            @PathVariable
            @Positive(message = "Job id must be positive")
            Long jobId,

            @RequestParam(required = false)
            ApplicationStatus status,

            @PageableDefault(
                    size = 20,
                    sort = "appliedAt",
                    direction = DESC
            )
            Pageable pageable
    ) {

        Page<JobApplicationResponse> response =
                recruiterJobApplicationService
                        .getApplicationsForJob(
                                jobId,
                                status,
                                pageable
                        );

        return ResponseEntity.ok(response);
    }
}