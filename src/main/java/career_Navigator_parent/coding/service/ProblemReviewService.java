package career_Navigator_parent.coding.service;

import career_Navigator_parent.coding.dto.request.ExecutionRequests;
import career_Navigator_parent.coding.dto.request.ExecutionRequests.Review;
import career_Navigator_parent.coding.dto.response.CodingResponses;
import career_Navigator_parent.coding.dto.response.StudentCodingResponses;
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