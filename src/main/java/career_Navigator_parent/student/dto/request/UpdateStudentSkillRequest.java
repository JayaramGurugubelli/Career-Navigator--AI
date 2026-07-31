package career_Navigator_parent.student.dto.request;

import career_Navigator_parent.student.enums.ProficiencyLevel;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStudentSkillRequest {

    @NotBlank
    @Size(max = 100)
    private String skillName;

    @NotNull
    private ProficiencyLevel proficiencyLevel;

    @Min(0)
    @Max(50)
    private Integer yearsOfExperience;

    @Size(max = 500)
    private String description;
}