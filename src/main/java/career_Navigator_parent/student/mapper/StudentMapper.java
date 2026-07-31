package career_Navigator_parent.student.mapper;

import career_Navigator_parent.student.dto.request.CreateStudentRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentRequest;
import career_Navigator_parent.student.dto.response.StudentResponse;
import career_Navigator_parent.student.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public Student toEntity(CreateStudentRequest request) {

        return Student.builder()
                .collegeName(request.getCollegeName())
                .universityName(request.getUniversityName())
                .degree(request.getDegree())
                .branch(request.getBranch())
                .cgpa(request.getCgpa())
                .graduationYear(request.getGraduationYear())
                .currentSemester(request.getCurrentSemester())
                .backlogs(request.getBacklogs())
                .activelyLooking(request.getActivelyLooking()).activelyLooking(request.getActivelyLooking() != null ? request.getActivelyLooking() : true)
                .build();
    }

    public StudentResponse toResponse(Student student) {

        return StudentResponse.builder()
                .id(student.getId())
                .userId(student.getUser().getId())
                .collegeName(student.getCollegeName())
                .universityName(student.getUniversityName())
                .degree(student.getDegree())
                .branch(student.getBranch())
                .cgpa(student.getCgpa())
                .graduationYear(student.getGraduationYear())
                .currentSemester(student.getCurrentSemester())
                .backlogs(student.getBacklogs())
                .activelyLooking(student.getActivelyLooking())
                .profileCompleted(student.getProfileCompleted())
                .build();
    }

    public void update(Student student, UpdateStudentRequest request) {

        student.setCollegeName(request.getCollegeName());
        student.setUniversityName(request.getUniversityName());
        student.setDegree(request.getDegree());
        student.setBranch(request.getBranch());
        student.setCgpa(request.getCgpa());
        student.setGraduationYear(request.getGraduationYear());
        student.setCurrentSemester(request.getCurrentSemester());
        student.setBacklogs(request.getBacklogs());
        student.setActivelyLooking(request.getActivelyLooking());
    }
}