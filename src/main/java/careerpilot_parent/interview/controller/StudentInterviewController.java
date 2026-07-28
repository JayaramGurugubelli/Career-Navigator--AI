package careerpilot_parent.interview.controller;

import careerpilot_parent.interview.dto.request.StudentInterviewResponseRequest;
import careerpilot_parent.interview.dto.response.InterviewResponse;
import careerpilot_parent.interview.enums.InterviewStatus;
import careerpilot_parent.interview.service.StudentInterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/interviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentInterviewController {

    private final StudentInterviewService
            studentInterviewService;

    @GetMapping
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
                studentInterviewService
                        .getMyInterviews(
                                status,
                                pageable
                        )
        );
    }

    @GetMapping("/{interviewId}")
    public ResponseEntity<InterviewResponse>
    getInterviewById(
            @PathVariable Long interviewId
    ) {

        return ResponseEntity.ok(
                studentInterviewService
                        .getInterviewById(
                                interviewId
                        )
        );
    }

    @PatchMapping(
            "/{interviewId}/confirm"
    )
    public ResponseEntity<InterviewResponse>
    confirmInterview(
            @PathVariable Long interviewId,

            @Valid
            @RequestBody
            StudentInterviewResponseRequest request
    ) {

        return ResponseEntity.ok(
                studentInterviewService
                        .confirmInterview(
                                interviewId,
                                request
                        )
        );
    }

    @PatchMapping(
            "/{interviewId}/decline"
    )
    public ResponseEntity<InterviewResponse>
    declineInterview(
            @PathVariable Long interviewId,

            @Valid
            @RequestBody
            StudentInterviewResponseRequest request
    ) {

        return ResponseEntity.ok(
                studentInterviewService
                        .declineInterview(
                                interviewId,
                                request
                        )
        );
    }
}