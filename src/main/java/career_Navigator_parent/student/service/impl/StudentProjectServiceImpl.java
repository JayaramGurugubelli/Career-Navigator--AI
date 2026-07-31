package career_Navigator_parent.student.service.impl;
import career_Navigator_parent.common.exception.DuplicateResourceException;
import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.common.exception.ValidationException;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.dto.request.CreateStudentProjectRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentProjectRequest;
import career_Navigator_parent.student.dto.response.StudentProjectResponse;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.entity.StudentProject;
import career_Navigator_parent.student.mapper.StudentProjectMapper;
import career_Navigator_parent.student.repository.StudentProjectRepository;
import career_Navigator_parent.student.repository.StudentRepository;
import career_Navigator_parent.student.service.StudentProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentProjectServiceImpl implements StudentProjectService {

    private final StudentRepository studentRepository;

    private final StudentProjectRepository projectRepository;

    private final StudentProjectMapper projectMapper;

    private final SecurityUtils securityUtils;

    /**
     * Returns currently logged-in student's profile.
     */
    private Student getCurrentStudent() {

        Long userId = securityUtils.getCurrentUserId();

        return studentRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student profile not found."));
    }

    /**
     * Validates project dates.
     */
    private void validateProjectDates(boolean currentlyWorking, java.time.LocalDate startDate, java.time.LocalDate endDate) {

        if (!currentlyWorking && endDate == null) {
            throw new ValidationException(
                    "End date is required when project is completed."
            );
        }

        if (endDate != null && endDate.isBefore(startDate)) {
            throw new ValidationException(
                    "End date cannot be before start date."
            );
        }
    }

    @Override
    public StudentProjectResponse addProject(CreateStudentProjectRequest request) {

        Student student = getCurrentStudent();

        if (projectRepository.existsByStudentIdAndProjectTitleIgnoreCase(
                student.getId(),
                request.getProjectTitle().trim())) {

            throw new DuplicateResourceException(
                    "Project already exists."
            );
        }

        validateProjectDates(request.getCurrentlyWorking(), request.getStartDate(), request.getEndDate());

        StudentProject project =
                projectMapper.toEntity(request, student);

        project = projectRepository.save(project);

        return projectMapper.toResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProjectResponse> getProjects() {

        Student student = getCurrentStudent();

        return projectRepository
                .findByStudentIdOrderByStartDateDesc(student.getId())
                .stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProjectResponse getProjectById(Long projectId) {

        Student student = getCurrentStudent();

        StudentProject project =
                projectRepository.findByIdAndStudentId(
                                projectId,
                                student.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found."
                                ));

        return projectMapper.toResponse(project);
    }

    @Override
    public StudentProjectResponse updateProject(Long projectId, UpdateStudentProjectRequest request) {

        Student student = getCurrentStudent();

        StudentProject project =
                projectRepository.findByIdAndStudentId(
                                projectId,
                                student.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found."
                                ));

        if (!project.getProjectTitle().equalsIgnoreCase(request.getProjectTitle().trim())
                && projectRepository.existsByStudentIdAndProjectTitleIgnoreCase(
                student.getId(),
                request.getProjectTitle().trim())) {

            throw new DuplicateResourceException(
                    "Project already exists."
            );
        }

        validateProjectDates(request.getCurrentlyWorking(), request.getStartDate(), request.getEndDate());

        projectMapper.update(project, request);

        project = projectRepository.save(project);

        return projectMapper.toResponse(project);
    }

    @Override
    public void deleteProject(Long projectId) {

        Student student = getCurrentStudent();

        StudentProject project =
                projectRepository.findByIdAndStudentId(
                                projectId,
                                student.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found."
                                ));

        projectRepository.delete(project);
    }

}