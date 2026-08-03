package career_Navigator_parent.coding.event;

import career_Navigator_parent.coding.service.SubmissionJudgingService;
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

    private final SubmissionJudgingService submissionJudgingService;

    @Async("submissionExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            SubmissionQueuedEvent event
    ) {
        Long submissionId =
                event.submissionId();

        log.info(
                "Submission evaluation started. submissionId={}",
                submissionId
        );

        try {
            submissionJudgingService.judge(
                    submissionId
            );

            log.info(
                    "Submission evaluation completed. submissionId={}",
                    submissionId
            );

        } catch (Exception exception) {

            String errorMessage =
                    resolveErrorMessage(exception);

            log.error(
                    "Submission evaluation failed. submissionId={}, error={}",
                    submissionId,
                    errorMessage,
                    exception
            );

            try {
                submissionJudgingService.markAsFailed(
                        submissionId,
                        errorMessage
                );

            } catch (Exception markingException) {
                log.error(
                        "Unable to mark submission as failed. submissionId={}",
                        submissionId,
                        markingException
                );
            }
        }
    }

    private String resolveErrorMessage(
            Exception exception
    ) {
        if (
                exception.getMessage() != null
                        && !exception.getMessage().isBlank()
        ) {
            return exception.getMessage();
        }

        Throwable cause =
                exception.getCause();

        if (
                cause != null
                        && cause.getMessage() != null
                        && !cause.getMessage().isBlank()
        ) {
            return cause.getMessage();
        }

        return exception
                .getClass()
                .getSimpleName();
    }
}