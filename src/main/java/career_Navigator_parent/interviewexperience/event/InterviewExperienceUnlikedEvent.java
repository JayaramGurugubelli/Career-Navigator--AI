package career_Navigator_parent.interviewexperience.event;

public record InterviewExperienceUnlikedEvent(
        Long experienceId,
        Long actorUserId,
        int likeCount
) {
}