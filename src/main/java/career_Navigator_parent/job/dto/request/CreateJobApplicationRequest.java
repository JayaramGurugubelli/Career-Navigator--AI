package career_Navigator_parent.job.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateJobApplicationRequest {

    @NotNull(message = "Resume id is required")
    private Long resumeId;

    @Size(
            max = 10000,
            message = "Cover letter must not exceed 10000 characters"
    )
    private String coverLetter;
}