package careerpilot_parent.coding.realtime;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseSubmissionEventPublisher implements SubmissionEventPublisher {

    private final ConcurrentHashMap<Long, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long studentId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.computeIfAbsent(studentId, ignored -> ConcurrentHashMap.newKeySet()).add(emitter);
        Runnable cleanup = () -> remove(studentId, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(error -> cleanup.run());
        return emitter;
    }

    @Override
    public void publish(Long studentId, SubmissionEvent event) {
        Set<SseEmitter> studentEmitters = emitters.get(studentId);
        if (studentEmitters == null) return;
        for (SseEmitter emitter : Set.copyOf(studentEmitters)) {
            try {
                emitter.send(SseEmitter.event()
                        .name("submission-status")
                        .id(String.valueOf(event.submissionId()))
                        .data(event));
            } catch (IOException exception) {
                remove(studentId, emitter);
            }
        }
    }

    private void remove(Long studentId, SseEmitter emitter) {
        Set<SseEmitter> set = emitters.get(studentId);
        if (set == null) return;
        set.remove(emitter);
        if (set.isEmpty()) emitters.remove(studentId);
    }
}
