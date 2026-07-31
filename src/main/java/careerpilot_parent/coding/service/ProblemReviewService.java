package careerpilot_parent.coding.service;

import careerpilot_parent.coding.dto.request.ExecutionRequests;
import careerpilot_parent.coding.dto.request.ExecutionRequests.Review;
import careerpilot_parent.coding.dto.response.CodingResponses;
import careerpilot_parent.coding.dto.response.StudentCodingResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProblemReviewService {

    CodingResponses.Review save(
            Long problemId,
            Review request
    );

    Page<CodingResponses.Review> list(
            Long problemId,
            Pageable pageable
    );

    CodingResponses.Review update(
            Long problemId,
            Long reviewId,
            ExecutionRequests.Review request
    );

    void delete(
            Long problemId,
            Long reviewId
    );

    StudentCodingResponses.ReviewHelpful toggleHelpful(
            Long problemId,
            Long reviewId
    );

}