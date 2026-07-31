package career_Navigator_parent.student.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStudentRequest {

    @Size(max = 150)
    private String collegeName;

    @Size(max = 100)
    private String universityName;

    @Size(max = 100)
    private String degree;

    @Size(max = 100)
    private String branch;

    @DecimalMin("0.00")
    @DecimalMax("10.00")
    private Double cgpa;

    @Min(1900)
    @Max(2100)
    private Integer graduationYear;

    @Min(1)
    @Max(12)
    private Integer currentSemester;

    @Min(0)
    private Integer backlogs;

    private Boolean activelyLooking;
}