package career_Navigator_parent.interviewexperience.controller;

import career_Navigator_parent.interviewexperience.dto.request.CreateInterviewCommentRequest;
import career_Navigator_parent.interviewexperience.dto.request.CreateInterviewReplyRequest;
import career_Navigator_parent.interviewexperience.dto.request.UpdateInterviewCommentRequest;
import career_Navigator_parent.interviewexperience.dto.response.InterviewCommentResponse;
import career_Navigator_parent.interviewexperience.dto.response.PageResponse;
import career_Navigator_parent.interviewexperience.service.InterviewExperienceCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.ASC;

@RestController
@RequestMapping(
        "/api/interview-experiences/{experienceId}/comments"
)
@RequiredArgsConstructor
public class InterviewExperienceCommentController {

    private final InterviewExperienceCommentService commentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InterviewCommentResponse>
    createComment(
            @PathVariable
            Long experienceId,

            @Valid
            @RequestBody
            CreateInterviewCommentRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        commentService.createComment(
                                experienceId,
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<PageResponse<InterviewCommentResponse>>
    getComments(
            @PathVariable
            Long experienceId,

            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = ASC
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                commentService.getComments(
                        experienceId,
                        pageable
                )
        );
    }

    @PostMapping("/{commentId}/replies")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InterviewCommentResponse>
    createReply(
            @PathVariable
            Long experienceId,

            @PathVariable
            Long commentId,

            @Valid
            @RequestBody
            CreateInterviewReplyRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        commentService.createReply(
                                experienceId,
                                commentId,
                                request
                        )
                );
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<PageResponse<InterviewCommentResponse>>
    getReplies(
            @PathVariable
            Long experienceId,

            @PathVariable
            Long commentId,

            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = ASC
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                commentService.getReplies(
                        experienceId,
                        commentId,
                        pageable
                )
        );
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InterviewCommentResponse>
    updateComment(
            @PathVariable
            Long experienceId,

            @PathVariable
            Long commentId,

            @Valid
            @RequestBody
            UpdateInterviewCommentRequest request
    ) {

        return ResponseEntity.ok(
                commentService.updateComment(
                        experienceId,
                        commentId,
                        request
                )
        );
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(
            @PathVariable
            Long experienceId,

            @PathVariable
            Long commentId
    ) {

        commentService.deleteComment(
                experienceId,
                commentId
        );

        return ResponseEntity.noContent().build();
    }
}