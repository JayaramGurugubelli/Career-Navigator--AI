package careerpilot_parent.coding.event;

import careerpilot_parent.coding.service.SubmissionJudgingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionQueuedEventListener {

    private final SubmissionJudgingService
            submissionJudgingService;

    @Async("submissionExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            SubmissionQueuedEvent event
    ) {
        try {
            submissionJudgingService.judge(
                    event.submissionId()
            );
        } catch (Exception exception) {
            log.error(
                    "Failed to judge submission ID: {}",
                    event.submissionId(),
                    exception
            );

            submissionJudgingService.markAsFailed(
                    event.submissionId(),
                    exception.getMessage()
            );
        }
    }
}