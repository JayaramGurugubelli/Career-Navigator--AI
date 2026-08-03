package career_Navigator_parent.learning.event;

import career_Navigator_parent.learning.realtime.LearningProgressPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class LearningProgressEventListener {
    private final LearningProgressPublisher publisher;

    @Async("learningProgressExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LearningProgressEvent event) {
        publisher.publish(event);
    }
}
