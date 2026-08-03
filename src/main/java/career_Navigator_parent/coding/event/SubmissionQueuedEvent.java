package career_Navigator_parent.coding.event;

public record SubmissionQueuedEvent(
        Long submissionId
) {

    public SubmissionQueuedEvent {

        if (
                submissionId == null
                        || submissionId <= 0
        ) {
            throw new IllegalArgumentException(
                    "A valid submission ID is required."
            );
        }
    }
}