package career_Navigator_parent.interviewexperience.event;

public record InterviewReplyCreatedEvent(
        Long experienceId,
        Long parentCommentId,
        Long replyId,
        Long actorUserId,
        Long parentCommentOwnerUserId,
        int replyCount
) {
}