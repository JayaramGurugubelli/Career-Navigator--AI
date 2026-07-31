package career_Navigator_parent.savedjob.controller;

import career_Navigator_parent.savedjob.dto.response.SavedJobHistoryResponse;
import career_Navigator_parent.savedjob.dto.response.SavedJobResponse;
import career_Navigator_parent.savedjob.dto.response.SavedJobStatusResponse;

import career_Navigator_parent.savedjob.enums.SavedJobAction;

import career_Navigator_parent.savedjob.service.SavedJobService;

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

@RestController
@RequestMapping("/api/student/saved-jobs")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('STUDENT')")
public class SavedJobController {

    private final SavedJobService
            savedJobService;

    @PostMapping("/{jobId}")
    public ResponseEntity<SavedJobResponse> saveJob(
            @PathVariable
            @Positive(message = "Job ID must be positive")
            Long jobId
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        savedJobService.saveJob(jobId)
                );
    }

    @GetMapping
    public ResponseEntity<Page<SavedJobResponse>>
    getSavedJobs(
            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            String location,

            @PageableDefault(
                    size = 20,
                    sort = "savedAt"
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                savedJobService.getSavedJobs(
                        keyword,
                        location,
                        pageable
                )
        );
    }

    @GetMapping("/{jobId}/status")
    public ResponseEntity<SavedJobStatusResponse>
    getSavedJobStatus(
            @PathVariable
            @Positive(message = "Job ID must be positive")
            Long jobId
    ) {

        return ResponseEntity.ok(
                savedJobService
                        .getSavedJobStatus(jobId)
        );
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> removeSavedJob(
            @PathVariable
            @Positive(message = "Job ID must be positive")
            Long jobId
    ) {

        savedJobService.removeSavedJob(jobId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/history")
    public ResponseEntity<
            Page<SavedJobHistoryResponse>
            >
    getSavedJobHistory(
            @RequestParam(required = false)
            SavedJobAction action,

            @PageableDefault(
                    size = 20,
                    sort = "actionAt"
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                savedJobService.getSavedJobHistory(
                        action,
                        pageable
                )
        );
    }
}