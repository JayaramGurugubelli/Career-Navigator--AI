package careerpilot_parent.company.dto.request;

import careerpilot_parent.company.enums.CurrencyCode;
import careerpilot_parent.company.enums.EmploymentType;
import careerpilot_parent.company.enums.ExperienceLevel;
import careerpilot_parent.company.enums.WorkMode;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateJobPostingRequest {

    @NotBlank(message = "Job title is required")
    @Size(
            max = 180,
            message = "Job title must not exceed 180 characters"
    )
    private String title;

    @NotBlank(message = "Job description is required")
    @Size(
            max = 20000,
            message = "Job description must not exceed 20000 characters"
    )
    private String description;

    @Size(max = 15000)
    private String responsibilities;

    @Size(max = 15000)
    private String qualifications;

    @Size(max = 15000)
    private String preferredQualifications;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @NotNull(message = "Work mode is required")
    private WorkMode workMode;

    @NotNull(message = "Experience level is required")
    private ExperienceLevel experienceLevel;

    @Size(max = 150)
    private String location;

    @Min(
            value = 0,
            message = "Minimum experience cannot be negative"
    )
    private Integer minimumExperience;

    @Min(
            value = 0,
            message = "Maximum experience cannot be negative"
    )
    private Integer maximumExperience;

    @DecimalMin(
            value = "0.0",
            message = "Minimum salary cannot be negative"
    )
    private BigDecimal minimumSalary;

    @DecimalMin(
            value = "0.0",
            message = "Maximum salary cannot be negative"
    )
    private BigDecimal maximumSalary;

    private CurrencyCode currency;

    private boolean salaryDisclosed;

    @NotEmpty(message = "At least one required skill is required")
    private Set<
            @NotBlank(message = "Skill cannot be blank")
            @Size(max = 100)
                    String
            > requiredSkills;

    @NotNull(message = "Number of openings is required")
    @Min(
            value = 1,
            message = "Number of openings must be at least 1"
    )
    private Integer numberOfOpenings;

    private LocalDate applicationDeadline;
}