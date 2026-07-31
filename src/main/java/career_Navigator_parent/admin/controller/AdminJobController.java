package career_Navigator_parent.admin.controller;

import career_Navigator_parent.admin.dto.request.UpdateJobStatusRequest;
import career_Navigator_parent.admin.dto.response.AdminJobResponse;
import career_Navigator_parent.admin.service.AdminJobService;
import career_Navigator_parent.company.enums.JobStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminJobController {

    private final AdminJobService adminJobService;

    @GetMapping
    public ResponseEntity<Page<AdminJobResponse>> getJobs(
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable
    ) {

        return ResponseEntity.ok(
                adminJobService.getJobs(
                        status,
                        keyword,
                        pageable
                )
        );
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<AdminJobResponse> getJobById(
            @PathVariable Long jobId
    ) {

        return ResponseEntity.ok(
                adminJobService.getJobById(jobId)
        );
    }

    @PatchMapping("/{jobId}/status")
    public ResponseEntity<AdminJobResponse> updateStatus(
            @PathVariable Long jobId,
            @Valid @RequestBody UpdateJobStatusRequest request
    ) {

        return ResponseEntity.ok(
                adminJobService.updateJobStatus(
                        jobId,
                        request
                )
        );
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long jobId
    ) {

        adminJobService.deleteJob(jobId);

        return ResponseEntity.noContent().build();
    }
}
