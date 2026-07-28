package careerpilot_parent.interviewexperience.dto.request;

import careerpilot_parent.interview.enums.InterviewType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInterviewRoundRequest {

    @NotNull(message = "Round number is required.")
    @Min(
            value = 1,
            message = "Round number must be at least 1."
    )
    private Integer roundNumber;

    @NotBlank(message = "Round title is required.")
    @Size(
            max = 150,
            message = "Round title cannot exceed 150 characters."
    )
    private String roundTitle;

    @NotNull(message = "Round type is required.")
    private InterviewType roundType;

    @Min(value = 1)
    @Max(value = 1440)
    private Integer durationMinutes;

    @NotNull(message = "Round display order is required.")
    @Min(value = 1)
    private Integer displayOrder;

    @Valid
    @NotEmpty(message = "At least one interview question is required.")
    @Size(max = 100)
    @Builder.Default
    private List<UpdateInterviewQuestionRequest> questions =
            new ArrayList<>();
}