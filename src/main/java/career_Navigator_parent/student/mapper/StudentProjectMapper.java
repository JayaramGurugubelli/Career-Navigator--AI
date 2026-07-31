package career_Navigator_parent.student.mapper;

import career_Navigator_parent.student.dto.request.CreateStudentProjectRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentProjectRequest;
import career_Navigator_parent.student.dto.response.StudentProjectResponse;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.entity.StudentProject;
import org.springframework.stereotype.Component;

@Component
public class StudentProjectMapper {

    /**
     * Convert Create DTO to Entity
     */
    public StudentProject toEntity(CreateStudentProjectRequest request, Student student) {

        return StudentProject.builder()
                .student(student)
                .projectTitle(request.getProjectTitle().trim())
                .projectType(request.getProjectType())
                .description(request.getDescription().trim())
                .technologiesUsed(request.getTechnologiesUsed().trim())
                .githubUrl(request.getGithubUrl())
                .liveDemoUrl(request.getLiveDemoUrl())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .currentlyWorking(request.getCurrentlyWorking())
                .build();
    }

    /**
     * Convert Entity to Response DTO
     */
    public StudentProjectResponse toResponse(StudentProject project) {

        return StudentProjectResponse.builder()
                .id(project.getId())
                .studentId(project.getStudent().getId())
                .projectTitle(project.getProjectTitle())
                .projectType(project.getProjectType())
                .description(project.getDescription())
                .technologiesUsed(project.getTechnologiesUsed())
                .githubUrl(project.getGithubUrl())
                .liveDemoUrl(project.getLiveDemoUrl())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .currentlyWorking(project.getCurrentlyWorking())
                .build();
    }

    /**
     * Update existing entity
     */
    public void update(StudentProject project, UpdateStudentProjectRequest request) {

        project.setProjectTitle(request.getProjectTitle().trim());
        project.setProjectType(request.getProjectType());
        project.setDescription(request.getDescription().trim());
        project.setTechnologiesUsed(request.getTechnologiesUsed().trim());
        project.setGithubUrl(request.getGithubUrl());
        project.setLiveDemoUrl(request.getLiveDemoUrl());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setCurrentlyWorking(request.getCurrentlyWorking());

    }

}