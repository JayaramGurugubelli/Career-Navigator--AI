package careerpilot_parent.interviewexperience.service;

import careerpilot_parent.interviewexperience.dto.request.CreateInterviewCommentRequest;
import careerpilot_parent.interviewexperience.dto.request.CreateInterviewReplyRequest;
import careerpilot_parent.interviewexperience.dto.request.UpdateInterviewCommentRequest;
import careerpilot_parent.interviewexperience.dto.response.InterviewCommentResponse;
import careerpilot_parent.interviewexperience.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface InterviewExperienceCommentService {

    /*
     * Creates a public top-level comment.
     *
     * Rules:
     * - Experience must be APPROVED.
     * - User must be authenticated.
     * - Email and phone numbers are rejected.
     */
    InterviewCommentResponse createComment(
            Long experienceId,
            CreateInterviewCommentRequest request
    );

    /*
     * Returns public top-level comments.
     * Replies are loaded separately.
     */
    PageResponse<InterviewCommentResponse> getComments(
            Long experienceId,
            Pageable pageable
    );

    /*
     * Creates a reply to a visible comment.
     *
     * For scalability, replies are restricted to one level.
     * If parent is already a reply, attach new reply to its root comment
     * or reject it based on your selected policy.
     */
    InterviewCommentResponse createReply(
            Long experienceId,
            Long parentCommentId,
            CreateInterviewReplyRequest request
    );

    /*
     * Returns paginated replies for one top-level comment.
     */
    PageResponse<InterviewCommentResponse> getReplies(
            Long experienceId,
            Long parentCommentId,
            Pageable pageable
    );

    /*
     * Only the comment owner can update their comment.
     */
    InterviewCommentResponse updateComment(
            Long experienceId,
            Long commentId,
            UpdateInterviewCommentRequest request
    );

    /*
     * Soft-delete.
     * Replies remain available.
     */
    void deleteComment(
            Long experienceId,
            Long commentId
    );

    /*
     * Admin operation for abusive or reported comments.
     */
    InterviewCommentResponse hideComment(
            Long experienceId,
            Long commentId
    );

    /*
     * Admin restores a previously hidden comment.
     */
    InterviewCommentResponse restoreComment(
            Long experienceId,
            Long commentId
    );

    long getVisibleCommentCount(
            Long experienceId
    );

    long getVisibleReplyCount(
            Long parentCommentId
    );
}