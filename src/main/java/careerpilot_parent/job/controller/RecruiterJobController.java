package careerpilot_parent.job.controller;


import careerpilot_parent.company.dto.request.CreateJobPostingRequest;
import careerpilot_parent.company.dto.request.UpdateJobPostingRequest;
import careerpilot_parent.company.dto.response.JobPostingResponse;


import careerpilot_parent.company.enums.JobStatus;
import careerpilot_parent.recruiter.service.RecruiterJobService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/recruiter/jobs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterJobController {

    private final RecruiterJobService recruiterJobService;

    @PostMapping
    public ResponseEntity<JobPostingResponse> createJob(
            @Valid
            @RequestBody
            CreateJobPostingRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        recruiterJobService
                                .createJob(request)
                );
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
                        JobStatus.valueOf(String.valueOf(status)),
                        pageable
                )
        );
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobPostingResponse> getMyJobById(
            @PathVariable Long jobId
    ) {

        return ResponseEntity.ok(
                recruiterJobService
                        .getMyJobById(jobId)
        );
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<JobPostingResponse> updateJob(
            @PathVariable Long jobId,

            @Valid
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
            @PathVariable Long jobId
    ) {

        return ResponseEntity.ok(
                recruiterJobService.publishJob(jobId)
        );
    }

    @PatchMapping("/{jobId}/pause")
    public ResponseEntity<JobPostingResponse> pauseJob(
            @PathVariable Long jobId
    ) {

        return ResponseEntity.ok(
                recruiterJobService.pauseJob(jobId)
        );
    }

    @PatchMapping("/{jobId}/close")
    public ResponseEntity<JobPostingResponse> closeJob(
            @PathVariable Long jobId
    ) {

        return ResponseEntity.ok(
                recruiterJobService.closeJob(jobId)
        );
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> archiveJob(
            @PathVariable Long jobId
    ) {

        recruiterJobService.archiveJob(jobId);

        return ResponseEntity
                .noContent()
                .build();
    }
}