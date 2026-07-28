package careerpilot_parent.interviewexperience.event;

import careerpilot_parent.interviewexperience.enums.InterviewExperienceStatus;

public record InterviewExperienceModeratedEvent(
        Long experienceId,
        Long ownerUserId,
        InterviewExperienceStatus status
) {
}