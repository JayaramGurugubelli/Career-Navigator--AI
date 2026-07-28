package careerpilot_parent.interviewexperience.realtime;

import java.time.LocalDateTime;

public record CommentRealtimeResponse(
        Long experienceId,
        Long commentId,
        Integer commentCount,
        String eventType,
        LocalDateTime occurredAt
) {
}