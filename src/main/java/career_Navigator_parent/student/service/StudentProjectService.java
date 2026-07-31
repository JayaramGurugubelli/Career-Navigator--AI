package career_Navigator_parent.student.service;

import career_Navigator_parent.student.dto.request.CreateStudentProjectRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentProjectRequest;
import career_Navigator_parent.student.dto.response.StudentProjectResponse;

import java.util.List;

public interface StudentProjectService {

    /**
     * Create Project
     */
    StudentProjectResponse addProject(CreateStudentProjectRequest request);

    /**
     * Get All Projects
     */
    List<StudentProjectResponse> getProjects();

    /**
     * Get Project By Id
     */
    StudentProjectResponse getProjectById(Long projectId);

    /**
     * Update Project
     */
    StudentProjectResponse updateProject(Long projectId, UpdateStudentProjectRequest request);

    /**
     * Delete Project
     */
    void deleteProject(Long projectId);

}