package careerpilot_parent.job.dto.request;

import careerpilot_parent.company.enums.JobApplicationStatus;

import jakarta.validation.constraints.NotNull;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateJobApplicationStatusRequest {

    @NotNull(message = "Job application status is required")
    private JobApplicationStatus status;
}