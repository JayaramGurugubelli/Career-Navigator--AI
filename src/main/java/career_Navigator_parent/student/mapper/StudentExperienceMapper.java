package career_Navigator_parent.student.mapper;

import career_Navigator_parent.student.dto.request.CreateStudentExperienceRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentExperienceRequest;
import career_Navigator_parent.student.dto.response.StudentExperienceResponse;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.entity.StudentExperience;
import org.springframework.stereotype.Component;

@Component
public class StudentExperienceMapper {

    /**
     * Convert Create Request to Entity
     */
    public StudentExperience toEntity(CreateStudentExperienceRequest request, Student student) {

        return StudentExperience.builder()
                .student(student)
                .companyName(request.getCompanyName())
                .jobTitle(request.getJobTitle())
                .employmentType(request.getEmploymentType())
                .location(request.getLocation())
                .currentlyWorking(request.getCurrentlyWorking())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .technologies(request.getTechnologies())
                .description(request.getDescription())
                .build();
    }

    /**
     * Convert Entity to Response
     */
    public StudentExperienceResponse toResponse(StudentExperience experience) {

        return StudentExperienceResponse.builder()
                .id(experience.getId())
                .companyName(experience.getCompanyName())
                .jobTitle(experience.getJobTitle())
                .employmentType(experience.getEmploymentType())
                .location(experience.getLocation())
                .currentlyWorking(experience.getCurrentlyWorking())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .technologies(experience.getTechnologies())
                .description(experience.getDescription())
                .build();
    }

    /**
     * Update Existing Entity
     */
    public void updateEntity(StudentExperience experience, UpdateStudentExperienceRequest request) {

        experience.setCompanyName(request.getCompanyName());
        experience.setJobTitle(request.getJobTitle());
        experience.setEmploymentType(request.getEmploymentType());
        experience.setLocation(request.getLocation());
        experience.setCurrentlyWorking(request.getCurrentlyWorking());
        experience.setStartDate(request.getStartDate());
        experience.setEndDate(request.getEndDate());
        experience.setTechnologies(request.getTechnologies());
        experience.setDescription(request.getDescription());
    }
}