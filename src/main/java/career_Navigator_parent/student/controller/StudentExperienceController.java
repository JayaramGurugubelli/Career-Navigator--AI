package career_Navigator_parent.student.controller;


import career_Navigator_parent.student.dto.request.CreateStudentExperienceRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentExperienceRequest;
import career_Navigator_parent.student.dto.response.StudentExperienceResponse;
import career_Navigator_parent.student.service.StudentExperienceService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/student/experiences")
@RequiredArgsConstructor
public class StudentExperienceController {


    private final StudentExperienceService experienceService;



    /**
     * Create Student Experience
     */
    @PostMapping
    public ResponseEntity<StudentExperienceResponse> createExperience(@Valid @RequestBody CreateStudentExperienceRequest request) {
        StudentExperienceResponse response = experienceService.createExperience(request);
        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }




    /**
     * Update Student Experience
     */
    @PutMapping("/{experienceId}")
    public ResponseEntity<StudentExperienceResponse> updateExperience(@PathVariable Long experienceId, @Valid @RequestBody UpdateStudentExperienceRequest request) {
        StudentExperienceResponse response = experienceService.updateExperience(experienceId, request);
        return ResponseEntity.ok(response);
    }





    /**
     * Get Experience By Id
     */
    @GetMapping("/{experienceId}")
    public ResponseEntity<StudentExperienceResponse> getExperienceById(@PathVariable Long experienceId) {
        StudentExperienceResponse response = experienceService.getExperienceById(experienceId);
        return ResponseEntity.ok(response);
    }





    /**
     * Get All Experiences
     */
    @GetMapping
    public ResponseEntity<List<StudentExperienceResponse>> getAllExperiences() {


        List<StudentExperienceResponse> experiences = experienceService.getAllExperiences();


        return ResponseEntity.ok(experiences);
    }





    /**
     * Delete Experience
     */
    @DeleteMapping("/{experienceId}")
    public ResponseEntity<Void> deleteExperience(@PathVariable Long experienceId) {

        experienceService.deleteExperience(experienceId);

        return ResponseEntity.noContent().build();
    }

}