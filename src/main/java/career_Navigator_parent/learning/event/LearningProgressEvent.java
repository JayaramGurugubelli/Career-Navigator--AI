package career_Navigator_parent.learning.event;

import java.time.LocalDateTime;
import java.util.Map;

public record LearningProgressEvent(Long studentId,String eventType,Long targetId,String targetType,
                                    Map<String,Object> payload,LocalDateTime occurredAt) {
    public LearningProgressEvent {
        payload = payload == null ? Map.of() : Map.copyOf(payload);
        occurredAt = occurredAt == null ? LocalDateTime.now() : occurredAt;
    }
}
