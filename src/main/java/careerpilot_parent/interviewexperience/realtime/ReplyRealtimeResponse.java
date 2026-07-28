package careerpilot_parent.interviewexperience.realtime;

import java.time.LocalDateTime;

public record ReplyRealtimeResponse(
        Long experienceId,
        Long parentCommentId,
        Long replyId,
        Integer replyCount,
        String eventType,
        LocalDateTime occurredAt
) {
}