package career_Navigator_parent.coding.controller;

import career_Navigator_parent.coding.dto.request.SubmitCodeRequest;
import career_Navigator_parent.coding.dto.response.CodingResponses.Submission;
import career_Navigator_parent.coding.dto.response.SubmissionAcceptedResponse;
import career_Navigator_parent.coding.service.AsyncSubmissionService;
import career_Navigator_parent.coding.service.SubmissionJudgingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/coding/submissions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentSubmissionController {

    private final AsyncSubmissionService asyncSubmissionService;
    private final SubmissionJudgingService submissionJudgingService;

    @PostMapping
    public ResponseEntity<SubmissionAcceptedResponse> submit(
            @Valid
            @RequestBody
            SubmitCodeRequest request
    ) {
        SubmissionAcceptedResponse response =
                asyncSubmissionService.enqueue(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }

    @GetMapping("/{submissionId:\\d+}")
    public ResponseEntity<Submission> getSubmission(
            @PathVariable
            Long submissionId
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

            @PageableDefault(size = 20)
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