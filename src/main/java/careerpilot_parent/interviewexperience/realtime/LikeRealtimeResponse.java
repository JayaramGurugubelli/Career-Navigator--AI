package careerpilot_parent.interviewexperience.realtime;

import java.time.LocalDateTime;

public record LikeRealtimeResponse(
        Long experienceId,
        Integer likeCount,
        Boolean liked,
        Long actorUserId,
        LocalDateTime occurredAt
) {
}