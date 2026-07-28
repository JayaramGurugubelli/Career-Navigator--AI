package careerpilot_parent.job.controller;

import careerpilot_parent.job.dto.request.CreateJobApplicationRequest;
import careerpilot_parent.job.dto.request.WithdrawJobApplicationRequest;
import careerpilot_parent.job.dto.response.JobApplicationResponse;
import careerpilot_parent.job.service.JobApplicationService;

import careerpilot_parent.shared.enums.ApplicationStatus;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class JobApplicationController {

    private final JobApplicationService
            jobApplicationService;

    @PostMapping("/jobs/{jobId}/applications")
    public ResponseEntity<JobApplicationResponse>
    applyForJob(
            @PathVariable Long jobId,

            @Valid
            @RequestBody
            CreateJobApplicationRequest request
    ) {

        JobApplicationResponse response =
                jobApplicationService.applyForJob(
                        jobId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/applications")
    public ResponseEntity<Page<JobApplicationResponse>>
    getMyApplications(
            @RequestParam(required = false)
            ApplicationStatus status,

            @PageableDefault(
                    size = 20,
                    sort = "appliedAt",
                    direction = DESC
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                jobApplicationService
                        .getMyApplications(
                                status,
                                pageable
                        )
        );
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<JobApplicationResponse>
    getMyApplicationById(
            @PathVariable Long applicationId
    ) {

        return ResponseEntity.ok(
                jobApplicationService
                        .getMyApplicationById(
                                applicationId
                        )
        );
    }

    @PatchMapping(
            "/applications/{applicationId}/withdraw"
    )
    public ResponseEntity<JobApplicationResponse>
    withdrawApplication(
            @PathVariable Long applicationId,

            @Valid
            @RequestBody(required = false)
            WithdrawJobApplicationRequest request
    ) {

        String reason =
                request == null
                        ? null
                        : request.getReason();

        return ResponseEntity.ok(
                jobApplicationService
                        .withdrawApplication(
                                applicationId,
                                reason
                        )
        );
    }

    @GetMapping("/jobs/{jobId}/has-applied")
    public ResponseEntity<Boolean> hasApplied(
            @PathVariable Long jobId
    ) {

        return ResponseEntity.ok(
                jobApplicationService
                        .hasApplied(jobId)
        );
    }
}