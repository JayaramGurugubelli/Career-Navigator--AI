package careerpilot_parent.interviewexperience.realtime;

import careerpilot_parent.interviewexperience.enums.InterviewExperienceStatus;

import java.time.LocalDateTime;

public record ModerationRealtimeResponse(
        Long experienceId,
        InterviewExperienceStatus status,
        LocalDateTime occurredAt
) {
}