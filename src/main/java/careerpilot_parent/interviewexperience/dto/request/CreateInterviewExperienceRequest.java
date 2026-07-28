package careerpilot_parent.interviewexperience.dto.request;

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
public class CreateInterviewExperienceRequest {

    /*
     * Optional when the company already exists in your Company table.
     */
    @Positive(message = "Company ID must be positive.")
    private Long companyId;

    /*
     * Required even if companyId is supplied.
     * It also preserves the company name as it existed when posted.
     */
    @NotBlank(message = "Company name is required.")
    @Size(
            max = 150,
            message = "Company name cannot exceed 150 characters."
    )
    private String companyName;

    @NotBlank(message = "Job role is required.")
    @Size(
            max = 150,
            message = "Job role cannot exceed 150 characters."
    )
    private String jobRole;

    @Size(
            max = 100,
            message = "Experience level cannot exceed 100 characters."
    )
    private String experienceLevel;

    @Size(
            max = 150,
            message = "Location cannot exceed 150 characters."
    )
    private String location;

    @Size(
            max = 5000,
            message = "Preparation tips cannot exceed 5000 characters."
    )
    private String preparationTips;

    @NotNull(message = "Anonymous preference is required.")
    private Boolean anonymous;

    @Valid
    @NotEmpty(message = "At least one interview round is required.")
    @Size(
            max = 20,
            message = "An experience cannot contain more than 20 rounds."
    )
    @Builder.Default
    private List<CreateInterviewRoundRequest> rounds =
            new ArrayList<>();
}