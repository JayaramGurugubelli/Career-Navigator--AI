package career_Navigator_parent.coding.realtime;

import career_Navigator_parent.coding.enums.SubmissionStatus;
import java.time.LocalDateTime;

public record SubmissionEvent(
        Long submissionId,
        SubmissionStatus status,
        Integer passedTestCases,
        Integer totalTestCases,
        String message,
        LocalDateTime occurredAt
) {}
