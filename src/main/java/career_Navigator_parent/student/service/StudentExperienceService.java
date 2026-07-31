package career_Navigator_parent.student.service;

import career_Navigator_parent.student.dto.request.CreateStudentExperienceRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentExperienceRequest;
import career_Navigator_parent.student.dto.response.StudentExperienceResponse;

import java.util.List;

public interface StudentExperienceService {

    StudentExperienceResponse createExperience(CreateStudentExperienceRequest request);

    StudentExperienceResponse updateExperience(Long experienceId, UpdateStudentExperienceRequest request);

    StudentExperienceResponse getExperienceById(Long experienceId);

    List<StudentExperienceResponse> getAllExperiences();

    void deleteExperience(Long experienceId);
}