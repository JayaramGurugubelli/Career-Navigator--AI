package career_Navigator_parent.interview.dto.request;

import career_Navigator_parent.interview.enums.InterviewResult;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInterviewFeedbackRequest {

    @NotNull(message = "Interview result is required")
    private InterviewResult result;

    @NotBlank(message = "Interview feedback is required")
    @Size(
            max = 5000,
            message = "Feedback must not exceed 5000 characters"
    )
    private String feedback;

    @Size(
            max = 3000,
            message = "Strengths must not exceed 3000 characters"
    )
    private String strengths;

    @Size(
            max = 3000,
            message = "Areas for improvement must not exceed 3000 characters"
    )
    private String areasForImprovement;

    @DecimalMin(
            value = "0.0",
            message = "Technical score cannot be negative"
    )
    @DecimalMax(
            value = "10.0",
            message = "Technical score cannot exceed 10"
    )
    private Double technicalScore;

    @DecimalMin(
            value = "0.0",
            message = "Communication score cannot be negative"
    )
    @DecimalMax(
            value = "10.0",
            message = "Communication score cannot exceed 10"
    )
    private Double communicationScore;

    @DecimalMin(
            value = "0.0",
            message = "Problem-solving score cannot be negative"
    )
    @DecimalMax(
            value = "10.0",
            message = "Problem-solving score cannot exceed 10"
    )
    private Double problemSolvingScore;

    @DecimalMin(
            value = "0.0",
            message = "Overall score cannot be negative"
    )
    @DecimalMax(
            value = "10.0",
            message = "Overall score cannot exceed 10"
    )
    private Double overallScore;
}