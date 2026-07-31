package career_Navigator_parent.interviewexperience.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewInterviewExperienceReportRequest {

    @NotNull(message = "Resolved status is required.")
    private Boolean resolved;

    @Size(
            max = 1000,
            message = "Admin notes cannot exceed 1000 characters."
    )
    private String adminNotes;
}