package careerpilot_parent.interviewexperience.dto.request;

import careerpilot_parent.interviewexperience.enums.InterviewExperienceStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModerateInterviewExperienceRequest {

    @NotNull(message = "Experience status is required.")
    private InterviewExperienceStatus status;

    @Size(
            max = 1000,
            message = "Moderation notes cannot exceed 1000 characters."
    )
    private String moderationNotes;
}