package career_Navigator_parent.learning.realtime;

import career_Navigator_parent.learning.event.LearningProgressEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface LearningProgressPublisher {
    SseEmitter subscribe(Long studentId);
    void publish(LearningProgressEvent event);
}
