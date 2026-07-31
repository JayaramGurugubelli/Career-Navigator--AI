package career_Navigator_parent.student.mapper;

import career_Navigator_parent.student.dto.request.StudentEducationRequest;
import career_Navigator_parent.student.dto.response.StudentEducationResponse;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.entity.StudentEducation;
import org.springframework.stereotype.Component;

@Component
public class StudentEducationMapper {
    public StudentEducation toEntity(StudentEducationRequest studentEducationRequest, Student student) {
        return StudentEducation.builder()
                .student(student)
                .degree(studentEducationRequest.getDegree())
                .specialization(studentEducationRequest.getSpecialization())
                .institutionName(studentEducationRequest.getInstitutionName())
                .university(studentEducationRequest.getUniversity())
                .board(studentEducationRequest.getBoard())
                .passingYear(studentEducationRequest.getPassingYear())
                .percentage(studentEducationRequest.getPercentage())
                .grade(studentEducationRequest.getGrade())
                .pursuing(studentEducationRequest.getPursuing())
                .startDate(studentEducationRequest.getStartDate())
                .endDate(studentEducationRequest.getEndDate())
                .description(studentEducationRequest.getDescription())
                .build();
    }
    public StudentEducationResponse toResponse(StudentEducation education) {
        return StudentEducationResponse.builder()
                .id(education.getId())
                .degree(education.getDegree())
                .specialization(education.getSpecialization())
                .institutionName(education.getInstitutionName())
                .university(education.getUniversity())
                .board(education.getBoard())
                .passingYear(education.getPassingYear())
                .percentage(education.getPercentage())
                .grade(education.getGrade())
                .pursuing(education.getPursuing())
                .startDate(education.getStartDate())
                .endDate(education.getEndDate())
                .description(education.getDescription())
                .build();
    }
    public void updateEntity(StudentEducation education, StudentEducationRequest request){
        education.setDegree(request.getDegree());
        education.setSpecialization(request.getSpecialization());
        education.setInstitutionName(request.getInstitutionName());
        education.setUniversity(request.getUniversity());
        education.setBoard(request.getBoard());
        education.setPassingYear(request.getPassingYear());
        education.setPercentage(request.getPercentage());
        education.setGrade(request.getGrade());
        education.setPursuing(request.getPursuing());
        education.setStartDate(request.getStartDate());
        education.setEndDate(request.getEndDate());
        education.setDescription(request.getDescription());
    }
}
