package careerpilot_parent.coding.controller;

import careerpilot_parent.coding.dto.request.ExecutionRequests.Submit;
import careerpilot_parent.coding.dto.response.CodingResponses.Submission;
import careerpilot_parent.coding.service.SubmissionJudgingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/coding/submissions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentSubmissionController {

    private final SubmissionJudgingService submissionJudgingService;

    @PostMapping
    public ResponseEntity<Submission> submit(
            @Valid @RequestBody Submit request
    ) {

        Submission response =
                submissionJudgingService.submit(request);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }

    @GetMapping("/{submissionId:\\d+}")
    public ResponseEntity<Submission> getSubmission(
            @PathVariable Long submissionId
    ) {

        return ResponseEntity.ok(
                submissionJudgingService.get(
                        submissionId
                )
        );
    }

    @GetMapping
    public ResponseEntity<Page<Submission>> getSubmissionHistory(
            @RequestParam(required = false)
            Long problemId,

            @PageableDefault(
                    size = 20,
                    sort = "submittedAt"
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                submissionJudgingService.history(
                        problemId,
                        pageable
                )
        );
    }
}