package career_Navigator_parent.student.service;

import career_Navigator_parent.student.dto.request.StudentEducationRequest;
import career_Navigator_parent.student.dto.response.StudentEducationResponse;

import java.util.List;

public interface StudentEducationService {
    StudentEducationResponse addEducation(StudentEducationRequest request);

    List<StudentEducationResponse> getEducations();

    StudentEducationResponse getEducationById(Long educationId);

    StudentEducationResponse updateEducation(Long educationId, StudentEducationRequest request);

    void deleteEducation(Long educationId);
}
