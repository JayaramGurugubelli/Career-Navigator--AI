package career_Navigator_parent.interviewexperience.event;

public record InterviewExperienceLikedEvent(
        Long experienceId,
        Long actorUserId,
        Long experienceOwnerUserId,
        int likeCount
) {
}