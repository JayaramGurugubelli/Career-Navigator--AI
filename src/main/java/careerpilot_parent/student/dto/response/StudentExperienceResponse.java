package careerpilot_parent.student.dto.response;

import careerpilot_parent.company.enums.EmploymentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentExperienceResponse {

    private Long id;

    private Long studentId;

    private String companyName;

    private String jobTitle;

    private EmploymentType employmentType;

    private String location;

    private Boolean currentlyWorking;

    private LocalDate startDate;

    private LocalDate endDate;

    private String technologies;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}