package career_Navigator_parent.student.controller;

import career_Navigator_parent.student.dto.request.StudentEducationRequest;
import career_Navigator_parent.student.dto.response.StudentEducationResponse;
import career_Navigator_parent.student.service.StudentEducationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/educations")
@RequiredArgsConstructor
public class StudentEducationController {

    private final StudentEducationService studentEducationService;

    /**
     * Add Education
     */
    @PostMapping
    public ResponseEntity<StudentEducationResponse> addEducation(@Valid @RequestBody StudentEducationRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(studentEducationService.addEducation(request));
    }

    /**
     * Get All Educations
     */
    @GetMapping
    public ResponseEntity<List<StudentEducationResponse>> getEducations() {

        return ResponseEntity.ok(
                studentEducationService.getEducations()
        );
    }

    /**
     * Get Education By Id
     */
    @GetMapping("/{educationId}")
    public ResponseEntity<StudentEducationResponse> getEducationById(@PathVariable Long educationId) {

        return ResponseEntity.ok(
                studentEducationService.getEducationById(educationId)
        );
    }

    /**
     * Update Education
     */
    @PutMapping("/{educationId}")
    public ResponseEntity<StudentEducationResponse> updateEducation(@PathVariable Long educationId, @Valid @RequestBody StudentEducationRequest request) {

        return ResponseEntity.ok(
                studentEducationService.updateEducation(
                        educationId,
                        request
                )
        );
    }

    /**
     * Delete Education
     */
    @DeleteMapping("/{educationId}")
    public ResponseEntity<String> deleteEducation(@PathVariable Long educationId) {

        studentEducationService.deleteEducation(educationId);

        return ResponseEntity.ok("Education deleted successfully.");
    }

}