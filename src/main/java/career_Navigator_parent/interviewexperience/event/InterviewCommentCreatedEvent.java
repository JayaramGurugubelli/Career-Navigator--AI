package career_Navigator_parent.interviewexperience.event;

public record InterviewCommentCreatedEvent(
        Long experienceId,
        Long commentId,
        Long actorUserId,
        Long experienceOwnerUserId,
        int commentCount
) {
}