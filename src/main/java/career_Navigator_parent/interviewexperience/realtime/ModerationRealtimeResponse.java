package career_Navigator_parent.interviewexperience.realtime;

import career_Navigator_parent.interviewexperience.enums.InterviewExperienceStatus;

import java.time.LocalDateTime;

public record ModerationRealtimeResponse(
        Long experienceId,
        InterviewExperienceStatus status,
        LocalDateTime occurredAt
) {
}