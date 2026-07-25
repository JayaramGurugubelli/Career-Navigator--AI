package careerpilot_parent.student.dto.request;

import careerpilot_parent.company.enums.EmploymentType;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStudentExperienceRequest {

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

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @Size(
            max = 150,
            message = "Location must not exceed 150 characters"
    )
    private String location;

    @NotNull(message = "Currently working value is required")
    private Boolean currentlyWorking;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @Size(
            max = 5000,
            message = "Technologies must not exceed 5000 characters"
    )
    private String technologies;

    @Size(
            max = 10000,
            message = "Description must not exceed 10000 characters"
    )
    private String description;

    @AssertTrue(
            message = "End date is required when currently working is false"
    )
    public boolean isEndDateValid() {

        if (Boolean.TRUE.equals(currentlyWorking)) {
            return endDate == null;
        }

        return endDate != null;
    }

    @AssertTrue(
            message = "End date cannot be before start date"
    )
    public boolean isDateRangeValid() {

        if (startDate == null || endDate == null) {
            return true;
        }

        return !endDate.isBefore(startDate);
    }
}