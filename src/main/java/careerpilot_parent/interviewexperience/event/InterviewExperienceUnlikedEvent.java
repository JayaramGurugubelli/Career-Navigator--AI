package careerpilot_parent.interviewexperience.event;

public record InterviewExperienceUnlikedEvent(
        Long experienceId,
        Long actorUserId,
        int likeCount
) {
}