package career_Navigator_parent.learning.realtime;

import career_Navigator_parent.learning.event.LearningProgressEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseLearningProgressPublisher implements LearningProgressPublisher {
    private static final long TIMEOUT = 30L * 60L * 1000L;
    private final Map<Long,CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(Long studentId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitters.computeIfAbsent(studentId,k -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> remove(studentId, emitter));
        emitter.onTimeout(() -> { remove(studentId, emitter); emitter.complete(); });
        emitter.onError(error -> remove(studentId, emitter));
        try {
            emitter.send(SseEmitter.event().name("CONNECTED").data(Map.of("studentId",studentId)));
        } catch (IOException ex) {
            remove(studentId, emitter);
            emitter.completeWithError(ex);
        }
        return emitter;
    }

    @Override
    public void publish(LearningProgressEvent event) {
        for (SseEmitter emitter : emitters.getOrDefault(event.studentId(),new CopyOnWriteArrayList<>())) {
            try {
                emitter.send(SseEmitter.event().name(event.eventType()).data(event));
            } catch (IOException ex) {
                remove(event.studentId(), emitter);
                emitter.completeWithError(ex);
            }
        }
    }

    private void remove(Long studentId,SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> list = emitters.get(studentId);
        if (list == null) return;
        list.remove(emitter);
        if (list.isEmpty()) emitters.remove(studentId);
    }
}
