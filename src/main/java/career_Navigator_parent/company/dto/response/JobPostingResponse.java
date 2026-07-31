package career_Navigator_parent.company.dto.response;

import career_Navigator_parent.company.enums.CurrencyCode;
import career_Navigator_parent.company.enums.EmploymentType;
import career_Navigator_parent.company.enums.ExperienceLevel;
import career_Navigator_parent.company.enums.JobStatus;
import career_Navigator_parent.company.enums.WorkMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostingResponse {

    private Long id;

    private String title;

    private String slug;

    private String description;

    private String responsibilities;

    private String qualifications;

    private String preferredQualifications;

    private EmploymentType employmentType;

    private WorkMode workMode;

    private ExperienceLevel experienceLevel;

    private String location;

    private Integer minimumExperience;

    private Integer maximumExperience;

    private BigDecimal minimumSalary;

    private BigDecimal maximumSalary;

    private CurrencyCode currency;

    private boolean salaryDisclosed;

    private Set<String> requiredSkills;

    private Integer numberOfOpenings;

    private LocalDate applicationDeadline;

    private JobStatus status;

    private LocalDateTime publishedAt;

    private LocalDateTime closedAt;

    private Long viewCount;

    private Long applicationCount;

    private CompanyResponse company;

    private Long recruiterId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}