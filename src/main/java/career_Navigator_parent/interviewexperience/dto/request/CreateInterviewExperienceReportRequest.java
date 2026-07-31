package career_Navigator_parent.interviewexperience.dto.request;

import career_Navigator_parent.interviewexperience.enums.InterviewExperienceReportReason;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInterviewExperienceReportRequest {

    @NotNull(message = "Report reason is required.")
    private InterviewExperienceReportReason reason;

    @Size(
            max = 1000,
            message = "Additional details cannot exceed 1000 characters."
    )
    private String additionalDetails;
}