package career_Navigator_parent.student.service;

import career_Navigator_parent.student.dto.request.CreateStudentSkillRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentSkillRequest;
import career_Navigator_parent.student.dto.response.StudentSkillResponse;

import java.util.List;

public interface StudentSkillService {

    StudentSkillResponse addSkill(CreateStudentSkillRequest request);

    List<StudentSkillResponse> getSkills();

    StudentSkillResponse getSkillById(Long skillId);

    StudentSkillResponse updateSkill(Long skillId, UpdateStudentSkillRequest request);

    void deleteSkill(Long skillId);

}