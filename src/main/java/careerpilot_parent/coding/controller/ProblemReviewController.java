package careerpilot_parent.coding.controller;

import careerpilot_parent.coding.dto.request.ExecutionRequests.Review;
import careerpilot_parent.coding.dto.response.CodingResponses;
import careerpilot_parent.coding.dto.response.StudentCodingResponses;
import careerpilot_parent.coding.service.ProblemReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        "/api/coding/problems/{problemId:\\d+}/reviews"
)
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
public class ProblemReviewController {

    private final ProblemReviewService problemReviewService;

    @GetMapping
    public ResponseEntity<Page<CodingResponses.Review>>
    listReviews(
            @PathVariable
            @Positive
            Long problemId,

            @PageableDefault(size = 10)
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                problemReviewService.list(
                        problemId,
                        pageable
                )
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CodingResponses.Review>
    createReview(
            @PathVariable
            @Positive
            Long problemId,

            @Valid
            @RequestBody
            Review request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        problemReviewService.save(
                                problemId,
                                request
                        )
                );
    }

    @PutMapping("/{reviewId:\\d+}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CodingResponses.Review>
    updateReview(
            @PathVariable
            @Positive
            Long problemId,

            @PathVariable
            @Positive
            Long reviewId,

            @Valid
            @RequestBody
            Review request
    ) {
        return ResponseEntity.ok(
                problemReviewService.update(
                        problemId,
                        reviewId,
                        request
                )
        );
    }

    @DeleteMapping("/{reviewId:\\d+}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> deleteReview(
            @PathVariable
            @Positive
            Long problemId,

            @PathVariable
            @Positive
            Long reviewId
    ) {
        problemReviewService.delete(
                problemId,
                reviewId
        );

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/{reviewId:\\d+}/helpful")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<
            StudentCodingResponses.ReviewHelpful
            > toggleHelpful(
            @PathVariable
            @Positive
            Long problemId,

            @PathVariable
            @Positive
            Long reviewId
    ) {
        return ResponseEntity.ok(
                problemReviewService.toggleHelpful(
                        problemId,
                        reviewId
                )
        );
    }
}