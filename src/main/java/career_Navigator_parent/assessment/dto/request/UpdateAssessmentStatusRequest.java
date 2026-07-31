package career_Navigator_parent.assessment.dto.request;

import career_Navigator_parent.assessment.enums.AssessmentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAssessmentStatusRequest {

    @NotNull(message = "Assessment status is required")
    private AssessmentStatus status;

    @Size(
            max = 2000,
            message = "Comment cannot exceed 2000 characters"
    )
    private String comment;
}