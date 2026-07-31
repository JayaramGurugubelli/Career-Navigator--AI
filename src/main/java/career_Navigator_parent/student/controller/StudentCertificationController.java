package career_Navigator_parent.student.controller;


import career_Navigator_parent.student.dto.request.CreateStudentCertificationRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentCertificationRequest;
import career_Navigator_parent.student.dto.response.StudentCertificationResponse;
import career_Navigator_parent.student.service.StudentCertificationService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/student/certifications")
@RequiredArgsConstructor
public class StudentCertificationController {


    private final StudentCertificationService certificationService;



    /**
     * Create Student Certification
     */
    @PostMapping
    public ResponseEntity<StudentCertificationResponse> createCertification(@Valid @RequestBody CreateStudentCertificationRequest request) {


        StudentCertificationResponse response = certificationService.createCertification(request);


        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }




    /**
     * Get All Certifications
     */
    @GetMapping
    public ResponseEntity<List<StudentCertificationResponse>> getAllCertifications() {


        List<StudentCertificationResponse> certifications = certificationService.getAllCertifications();


        return ResponseEntity.ok(certifications);
    }





    /**
     * Get Certification By Id
     */
    @GetMapping("/{certificationId}")
    public ResponseEntity<StudentCertificationResponse> getCertificationById(@PathVariable Long certificationId) {
        StudentCertificationResponse response = certificationService.getCertificationById(certificationId);
        return ResponseEntity.ok(response);
    }





    /**
     * Update Certification
     */
    @PutMapping("/{certificationId}")
    public ResponseEntity<StudentCertificationResponse> updateCertification(@PathVariable Long certificationId, @Valid @RequestBody UpdateStudentCertificationRequest request) {


        StudentCertificationResponse response = certificationService.updateCertification(certificationId, request);


        return ResponseEntity.ok(response);
    }





    /**
     * Delete Certification
     */
    @DeleteMapping("/{certificationId}")
    public ResponseEntity<?> deleteCertification(@PathVariable Long certificationId) {


        certificationService.deleteCertification(certificationId);



        return ResponseEntity.ok(
                "Certification deleted successfully"
        );
    }

}