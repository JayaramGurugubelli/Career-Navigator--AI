package career_Navigator_parent.assessment.dto.request;

import career_Navigator_parent.assessment.enums.AssessmentProvider;
import career_Navigator_parent.assessment.enums.AssessmentType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAssessmentRequest {

    @NotBlank(message = "Assessment title is required")
    @Size(
            max = 150,
            message = "Assessment title cannot exceed 150 characters"
    )
    private String title;

    @Size(
            max = 5000,
            message = "Description cannot exceed 5000 characters"
    )
    private String description;

    @NotNull(message = "Assessment type is required")
    private AssessmentType assessmentType;

    @NotNull(message = "Assessment provider is required")
    private AssessmentProvider provider;

    @Size(
            max = 150,
            message = "External assessment ID cannot exceed 150 characters"
    )
    private String externalAssessmentId;

    @Size(
            max = 1000,
            message = "Assessment URL cannot exceed 1000 characters"
    )
    private String assessmentUrl;

    @NotNull(message = "Scheduled time is required")
    @Future(message = "Scheduled time must be in the future")
    private LocalDateTime scheduledAt;

    @NotNull(message = "Assessment expiry time is required")
    @Future(message = "Assessment expiry time must be in the future")
    private LocalDateTime availableUntil;

    @NotNull(message = "Assessment duration is required")
    @Min(
            value = 1,
            message = "Duration must be at least 1 minute"
    )
    @Max(
            value = 600,
            message = "Duration cannot exceed 600 minutes"
    )
    private Integer durationMinutes;

    @NotNull(message = "Maximum score is required")
    @DecimalMin(
            value = "1.0",
            message = "Maximum score must be at least 1"
    )
    private Double maximumScore;

    @NotNull(message = "Passing score is required")
    @DecimalMin(
            value = "0.0",
            message = "Passing score cannot be negative"
    )
    private Double passingScore;

    @Size(
            max = 10000,
            message = "Instructions cannot exceed 10000 characters"
    )
    private String instructions;
}