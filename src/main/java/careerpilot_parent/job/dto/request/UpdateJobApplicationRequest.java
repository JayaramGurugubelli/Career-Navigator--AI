package careerpilot_parent.job.dto.request;

import careerpilot_parent.shared.enums.ApplicationStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateJobApplicationRequest {

    @NotBlank(message = "Company name is required")
    @Size(
            max = 150,
            message = "Company name must not exceed 150 characters"
    )
    private String companyName;

    @NotBlank(message = "Job title is required")
    @Size(
            max = 150,
            message = "Job title must not exceed 150 characters"
    )
    private String jobTitle;

    @Size(
            max = 150,
            message = "Location must not exceed 150 characters"
    )
    private String location;

    @Size(
            max = 50,
            message = "Employment type must not exceed 50 characters"
    )
    private String employmentType;

    @Size(
            max = 1000,
            message = "Job URL must not exceed 1000 characters"
    )
    private String jobUrl;

    @NotNull(message = "Job application status is required")
    private ApplicationStatus status;

    private LocalDate appliedDate;

    private LocalDate followUpDate;

    private LocalDate interviewDate;

    @Size(
            max = 100,
            message = "Salary range must not exceed 100 characters"
    )
    private String salaryRange;

    private String jobDescription;

    private String notes;

    @Size(
            max = 150,
            message = "Contact person must not exceed 150 characters"
    )
    private String contactPerson;

    @Email(message = "Contact email must be valid")
    @Size(
            max = 150,
            message = "Contact email must not exceed 150 characters"
    )
    private String contactEmail;
}