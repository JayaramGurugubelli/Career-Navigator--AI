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
public class UpdateInterviewExperienceRequest {

    @Positive(message = "Company ID must be positive.")
    private Long companyId;

    @NotBlank(message = "Company name is required.")
    @Size(max = 150)
    private String companyName;

    @NotBlank(message = "Job role is required.")
    @Size(max = 150)
    private String jobRole;

    @Size(max = 100)
    private String experienceLevel;

    @Size(max = 150)
    private String location;

    @Size(max = 5000)
    private String preparationTips;

    @NotNull(message = "Anonymous preference is required.")
    private Boolean anonymous;

    @Valid
    @NotEmpty(message = "At least one interview round is required.")
    @Size(max = 20)
    @Builder.Default
    private List<UpdateInterviewRoundRequest> rounds =
            new ArrayList<>();
}