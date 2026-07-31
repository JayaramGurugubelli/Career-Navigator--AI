package career_Navigator_parent.interviewexperience.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInterviewCommentRequest {

    @NotBlank(message = "Comment cannot be empty.")
    @Size(
            max = 1000,
            message = "Comment cannot exceed 1000 characters."
    )
    private String content;
}