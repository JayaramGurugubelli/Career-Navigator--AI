package career_Navigator_parent.coding.event;

import career_Navigator_parent.coding.service.impl.ProblemImportProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProblemImportEventListener {

    private final ProblemImportProcessor processor;

    @Async("codingTaskExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleImportQueued(
            ProblemImportQueuedEvent event
    ) {

        processor.process(event.importId());
    }
}