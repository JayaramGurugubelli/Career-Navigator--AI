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
public class CreateInterviewRequest {

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
    @Min(
            value = 1,
            message = "Round number must be at least 1"
    )
    @Max(
            value = 20,
            message = "Round number must not exceed 20"
    )
    private Integer roundNumber;

    @NotNull(message = "Scheduled date and time are required")
    @Future(message = "Interview must be scheduled in the future")
    private LocalDateTime scheduledAt;

    @NotNull(message = "Interview end time is required")
    @Future(message = "Interview end time must be in the future")
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

    @Size(
            max = 1000,
            message = "Meeting URL must not exceed 1000 characters"
    )
    private String meetingUrl;

    @Size(
            max = 150,
            message = "Meeting ID must not exceed 150 characters"
    )
    private String meetingId;

    @Size(
            max = 150,
            message = "Meeting password must not exceed 150 characters"
    )
    private String meetingPassword;

    @Size(
            max = 500,
            message = "Location must not exceed 500 characters"
    )
    private String location;

    @NotBlank(message = "Interviewer name is required")
    @Size(
            max = 150,
            message = "Interviewer name must not exceed 150 characters"
    )
    private String interviewerName;

    @Email(message = "Interviewer email must be valid")
    @Size(
            max = 150,
            message = "Interviewer email must not exceed 150 characters"
    )
    private String interviewerEmail;

    @Size(
            max = 150,
            message = "Interviewer designation must not exceed 150 characters"
    )
    private String interviewerDesignation;

    @Size(
            max = 5000,
            message = "Instructions must not exceed 5000 characters"
    )
    private String instructions;
}