package careerpilot_parent.interviewexperience.dto.request;

import careerpilot_parent.interviewexperience.enums.InterviewQuestionCategory;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInterviewQuestionRequest {

    @NotBlank(message = "Interview question is required.")
    @Size(
            max = 5000,
            message = "Interview question cannot exceed 5000 characters."
    )
    private String question;

    @NotNull(message = "Question category is required.")
    private InterviewQuestionCategory category;

    @Size(
            max = 150,
            message = "Question topic cannot exceed 150 characters."
    )
    private String topic;

    @Size(
            max = 3000,
            message = "Additional details cannot exceed 3000 characters."
    )
    private String additionalDetails;

    @NotNull(message = "Question display order is required.")
    @Min(
            value = 1,
            message = "Question display order must be at least 1."
    )
    private Integer displayOrder;
}