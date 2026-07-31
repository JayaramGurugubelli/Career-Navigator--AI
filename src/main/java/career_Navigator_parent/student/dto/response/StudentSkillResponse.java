package career_Navigator_parent.student.dto.response;

import career_Navigator_parent.student.enums.ProficiencyLevel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSkillResponse {

    private Long id;

    private Long studentId;

    private String skillName;

    private ProficiencyLevel proficiencyLevel;

    private Integer yearsOfExperience;

    private String description;
}