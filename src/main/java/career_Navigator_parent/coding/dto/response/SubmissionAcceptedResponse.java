package career_Navigator_parent.coding.dto.response;

import career_Navigator_parent.coding.enums.SubmissionStatus;

import java.time.LocalDateTime;

public record SubmissionAcceptedResponse(

        Long submissionId,

        SubmissionStatus status,

        String message,

        LocalDateTime submittedAt

) {
}