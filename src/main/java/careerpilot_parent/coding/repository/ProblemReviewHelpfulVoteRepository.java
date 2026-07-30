package careerpilot_parent.coding.repository;

import careerpilot_parent.coding.entity.ProblemReviewHelpfulVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemReviewHelpfulVoteRepository
        extends JpaRepository<
        ProblemReviewHelpfulVote,
        Long
        > {

    Optional<ProblemReviewHelpfulVote>
    findByStudentIdAndReviewId(
            Long studentId,
            Long reviewId
    );

    boolean existsByStudentIdAndReviewId(
            Long studentId,
            Long reviewId
    );

    long countByReviewId(Long reviewId);

    void deleteByReviewId(Long reviewId);
}