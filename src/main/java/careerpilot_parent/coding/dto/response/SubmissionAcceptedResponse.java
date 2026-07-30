package careerpilot_parent.coding.dto.response;

import careerpilot_parent.coding.enums.SubmissionStatus;

import java.time.LocalDateTime;

public record SubmissionAcceptedResponse(

        Long submissionId,

        SubmissionStatus status,

        String message,

        LocalDateTime submittedAt

) {
}