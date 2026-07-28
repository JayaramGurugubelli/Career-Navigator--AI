package careerpilot_parent.interview.dto.request;

import careerpilot_parent.interview.enums.InterviewMode;
import careerpilot_parent.interview.enums.InterviewType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInterviewRequest {

    @NotBlank(message = "Interview title is required")
    @Size(
            max = 150,
            message = "Interview title must not exceed 150 characters"
    )
    private String title;

    @Size(
            max = 5000,
            message = "Description must not exceed 5000 characters"
    )
    private String description;

    @NotNull(message = "Interview type is required")
    private InterviewType interviewType;

    @NotNull(message = "Interview mode is required")
    private InterviewMode interviewMode;

    @NotNull(message = "Round number is required")
    @Min(value = 1, message = "Round number must be at least 1")
    @Max(value = 20, message = "Round number must not exceed 20")
    private Integer roundNumber;

    @NotNull(message = "Scheduled date and time are required")
    private LocalDateTime scheduledAt;

    @NotNull(message = "Interview end time is required")
    private LocalDateTime endAt;

    @NotNull(message = "Interview duration is required")
    @Min(
            value = 10,
            message = "Interview duration must be at least 10 minutes"
    )
    @Max(
            value = 480,
            message = "Interview duration must not exceed 480 minutes"
    )
    private Integer durationMinutes;

    @Size(max = 1000)
    private String meetingUrl;

    @Size(max = 150)
    private String meetingId;

    @Size(max = 150)
    private String meetingPassword;

    @Size(max = 500)
    private String location;

    @NotBlank(message = "Interviewer name is required")
    @Size(max = 150)
    private String interviewerName;

    @Email(message = "Interviewer email must be valid")
    @Size(max = 150)
    private String interviewerEmail;

    @Size(max = 150)
    private String interviewerDesignation;

    @Size(max = 5000)
    private String instructions;
}