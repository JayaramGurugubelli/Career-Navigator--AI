package careerpilot_parent.interviewexperience.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInterviewCommentRequest {

    @NotBlank(message = "Comment cannot be empty.")
    @Size(
            max = 1000,
            message = "Comment cannot exceed 1000 characters."
    )
    private String content;
}