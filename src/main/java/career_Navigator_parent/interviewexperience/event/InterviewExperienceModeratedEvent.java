package career_Navigator_parent.interviewexperience.event;

import career_Navigator_parent.interviewexperience.enums.InterviewExperienceStatus;

public record InterviewExperienceModeratedEvent(
        Long experienceId,
        Long ownerUserId,
        InterviewExperienceStatus status
) {
}