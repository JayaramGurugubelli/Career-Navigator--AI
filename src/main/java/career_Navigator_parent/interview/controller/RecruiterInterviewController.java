package career_Navigator_parent.interview.controller;

import career_Navigator_parent.interview.dto.request.CreateInterviewRequest;
import career_Navigator_parent.interview.dto.request.UpdateInterviewFeedbackRequest;
import career_Navigator_parent.interview.dto.request.UpdateInterviewRequest;
import career_Navigator_parent.interview.dto.request.UpdateInterviewStatusRequest;
import career_Navigator_parent.interview.dto.response.InterviewResponse;
import career_Navigator_parent.interview.enums.InterviewStatus;
import career_Navigator_parent.interview.service.RecruiterInterviewService;
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
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterInterviewController {

    private final RecruiterInterviewService
            recruiterInterviewService;

    @PostMapping(
            "/applications/{applicationId}/interviews"
    )
    public ResponseEntity<InterviewResponse>
    createInterview(
            @PathVariable Long applicationId,
            @Valid
            @RequestBody
            CreateInterviewRequest request
    ) {

        InterviewResponse response =
                recruiterInterviewService
                        .createInterview(
                                applicationId,
                                request
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/interviews")
    public ResponseEntity<Page<InterviewResponse>>
    getMyInterviews(
            @RequestParam(required = false)
            InterviewStatus status,

            @PageableDefault(
                    size = 20,
                    sort = "scheduledAt"
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                recruiterInterviewService
                        .getMyInterviews(
                                status,
                                pageable
                        )
        );
    }

    @GetMapping(
            "/interviews/{interviewId}"
    )
    public ResponseEntity<InterviewResponse>
    getInterviewById(
            @PathVariable Long interviewId
    ) {

        return ResponseEntity.ok(
                recruiterInterviewService
                        .getInterviewById(
                                interviewId
                        )
        );
    }

    @GetMapping(
            "/applications/{applicationId}/interviews"
    )
    public ResponseEntity<Page<InterviewResponse>>
    getApplicationInterviews(
            @PathVariable Long applicationId,

            @PageableDefault(
                    size = 20,
                    sort = "scheduledAt"
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                recruiterInterviewService
                        .getApplicationInterviews(
                                applicationId,
                                pageable
                        )
        );
    }

    @PutMapping(
            "/interviews/{interviewId}"
    )
    public ResponseEntity<InterviewResponse>
    updateInterview(
            @PathVariable Long interviewId,

            @Valid
            @RequestBody
            UpdateInterviewRequest request
    ) {

        return ResponseEntity.ok(
                recruiterInterviewService
                        .updateInterview(
                                interviewId,
                                request
                        )
        );
    }

    @PatchMapping(
            "/interviews/{interviewId}/status"
    )
    public ResponseEntity<InterviewResponse>
    updateInterviewStatus(
            @PathVariable Long interviewId,

            @Valid
            @RequestBody
            UpdateInterviewStatusRequest request
    ) {

        return ResponseEntity.ok(
                recruiterInterviewService
                        .updateInterviewStatus(
                                interviewId,
                                request
                        )
        );
    }

    @PatchMapping(
            "/interviews/{interviewId}/feedback"
    )
    public ResponseEntity<InterviewResponse>
    updateInterviewFeedback(
            @PathVariable Long interviewId,

            @Valid
            @RequestBody
            UpdateInterviewFeedbackRequest request
    ) {

        return ResponseEntity.ok(
                recruiterInterviewService
                        .updateInterviewFeedback(
                                interviewId,
                                request
                        )
        );
    }

    @DeleteMapping(
            "/interviews/{interviewId}"
    )
    public ResponseEntity<Void>
    deleteInterview(
            @PathVariable Long interviewId
    ) {

        recruiterInterviewService
                .deleteInterview(interviewId);

        return ResponseEntity.noContent().build();
    }
}