package career_Navigator_parent.student.mapper;

import career_Navigator_parent.student.dto.request.CreateStudentSkillRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentSkillRequest;
import career_Navigator_parent.student.dto.response.StudentSkillResponse;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.entity.StudentSkill;
import org.springframework.stereotype.Component;

@Component
public class StudentSkillMapper {

    public StudentSkill toEntity(CreateStudentSkillRequest request, Student student) {

        return StudentSkill.builder()
                .student(student)
                .skillName(request.getSkillName())
                .proficiencyLevel(request.getProficiencyLevel())
                .yearsOfExperience(request.getYearsOfExperience())
                .description(request.getDescription())
                .build();
    }

    public StudentSkillResponse toResponse(StudentSkill skill) {

        return StudentSkillResponse.builder()
                .id(skill.getId())
                .studentId(skill.getStudent().getId())
                .skillName(skill.getSkillName())
                .proficiencyLevel(skill.getProficiencyLevel())
                .yearsOfExperience(skill.getYearsOfExperience())
                .description(skill.getDescription())
                .build();
    }

    public void update(StudentSkill skill, UpdateStudentSkillRequest request) {

        skill.setSkillName(request.getSkillName());
        skill.setProficiencyLevel(request.getProficiencyLevel());
        skill.setYearsOfExperience(request.getYearsOfExperience());
        skill.setDescription(request.getDescription());

    }

}