package career_Navigator_parent.student.controller;

import career_Navigator_parent.student.dto.request.CreateStudentProjectRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentProjectRequest;
import career_Navigator_parent.student.dto.response.StudentProjectResponse;
import career_Navigator_parent.student.service.StudentProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/projects")
@RequiredArgsConstructor
public class StudentProjectController {

    private final StudentProjectService studentProjectService;

    /**
     * Add Project
     */
    @PostMapping
    public ResponseEntity<StudentProjectResponse> addProject(@Valid @RequestBody CreateStudentProjectRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(studentProjectService.addProject(request));
    }

    /**
     * Get All Projects
     */
    @GetMapping
    public ResponseEntity<List<StudentProjectResponse>> getProjects() {

        return ResponseEntity.ok(studentProjectService.getProjects());
    }

    /**
     * Get Project By Id
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<StudentProjectResponse> getProjectById(@PathVariable Long projectId) {

        return ResponseEntity.ok(studentProjectService.getProjectById(projectId));
    }

    /**
     * Update Project
     */
    @PutMapping("/{projectId}")
    public ResponseEntity<StudentProjectResponse> updateProject(@PathVariable Long projectId, @Valid @RequestBody UpdateStudentProjectRequest request) {

        return ResponseEntity.ok(studentProjectService.updateProject(projectId, request));
    }

    /**
     * Delete Project
     */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable Long projectId) {

        studentProjectService.deleteProject(projectId);

        return ResponseEntity.ok("Project deleted successfully.");
    }

}