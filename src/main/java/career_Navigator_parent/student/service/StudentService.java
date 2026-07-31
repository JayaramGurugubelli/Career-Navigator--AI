package career_Navigator_parent.student.service;

import career_Navigator_parent.student.dto.request.CreateStudentRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentRequest;
import career_Navigator_parent.student.dto.response.StudentResponse;

public interface StudentService {

    StudentResponse create(CreateStudentRequest request);

    StudentResponse getCurrentStudent();

    StudentResponse update(UpdateStudentRequest request);

    void delete();
}