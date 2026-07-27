package careerpilot_parent.assessment.dto.request;

import careerpilot_parent.assessment.enums.AssessmentResult;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAssessmentResultRequest {

    @NotNull(message = "Obtained score is required")
    @DecimalMin(
            value = "0.0",
            message = "Obtained score cannot be negative"
    )
    private Double obtainedScore;

    @NotNull(message = "Assessment result is required")
    private AssessmentResult result;

    @Size(
            max = 5000,
            message = "Result notes cannot exceed 5000 characters"
    )
    private String resultNotes;
}