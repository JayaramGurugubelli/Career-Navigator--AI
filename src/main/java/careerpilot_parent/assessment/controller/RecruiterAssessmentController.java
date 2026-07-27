package careerpilot_parent.assessment.controller;

import careerpilot_parent.assessment.dto.request.CreateAssessmentRequest;
import careerpilot_parent.assessment.dto.request.UpdateAssessmentRequest;
import careerpilot_parent.assessment.dto.request.UpdateAssessmentResultRequest;
import careerpilot_parent.assessment.dto.request.UpdateAssessmentStatusRequest;
import careerpilot_parent.assessment.dto.response.AssessmentResponse;
import careerpilot_parent.assessment.enums.AssessmentStatus;
import careerpilot_parent.assessment.service.RecruiterAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterAssessmentController {

    private final RecruiterAssessmentService
            recruiterAssessmentService;

    @PostMapping(
            "/applications/{applicationId}/assessments"
    )
    public ResponseEntity<AssessmentResponse>
    createAssessment(
            @PathVariable Long applicationId,
            @Valid
            @RequestBody
            CreateAssessmentRequest request
    ) {

        AssessmentResponse response =
                recruiterAssessmentService
                        .createAssessment(
                                applicationId,
                                request
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/assessments")
    public ResponseEntity<Page<AssessmentResponse>>
    getMyAssessments(
            @RequestParam(required = false)
            AssessmentStatus status,

            @PageableDefault(
                    size = 20,
                    sort = "scheduledAt"
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                recruiterAssessmentService
                        .getMyAssessments(
                                status,
                                pageable
                        )
        );
    }

    @GetMapping(
            "/applications/{applicationId}/assessments"
    )
    public ResponseEntity<Page<AssessmentResponse>>
    getAssessmentsForApplication(
            @PathVariable Long applicationId,

            @PageableDefault(
                    size = 20,
                    sort = "scheduledAt"
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                recruiterAssessmentService
                        .getAssessmentsForApplication(
                                applicationId,
                                pageable
                        )
        );
    }

    @GetMapping(
            "/assessments/{assessmentId}"
    )
    public ResponseEntity<AssessmentResponse>
    getAssessmentById(
            @PathVariable Long assessmentId
    ) {

        return ResponseEntity.ok(
                recruiterAssessmentService
                        .getAssessmentById(
                                assessmentId
                        )
        );
    }

    @PutMapping(
            "/assessments/{assessmentId}"
    )
    public ResponseEntity<AssessmentResponse>
    updateAssessment(
            @PathVariable Long assessmentId,

            @Valid
            @RequestBody
            UpdateAssessmentRequest request
    ) {

        return ResponseEntity.ok(
                recruiterAssessmentService
                        .updateAssessment(
                                assessmentId,
                                request
                        )
        );
    }

    @PatchMapping(
            "/assessments/{assessmentId}/status"
    )
    public ResponseEntity<AssessmentResponse>
    updateAssessmentStatus(
            @PathVariable Long assessmentId,

            @Valid
            @RequestBody
            UpdateAssessmentStatusRequest request
    ) {

        return ResponseEntity.ok(
                recruiterAssessmentService
                        .updateAssessmentStatus(
                                assessmentId,
                                request
                        )
        );
    }

    @PatchMapping(
            "/assessments/{assessmentId}/result"
    )
    public ResponseEntity<AssessmentResponse>
    updateAssessmentResult(
            @PathVariable Long assessmentId,

            @Valid
            @RequestBody
            UpdateAssessmentResultRequest request
    ) {

        return ResponseEntity.ok(
                recruiterAssessmentService
                        .updateAssessmentResult(
                                assessmentId,
                                request
                        )
        );
    }

    @DeleteMapping(
            "/assessments/{assessmentId}"
    )
    public ResponseEntity<Void>
    deleteAssessment(
            @PathVariable Long assessmentId
    ) {

        recruiterAssessmentService
                .deleteAssessment(
                        assessmentId
                );

        return ResponseEntity.noContent().build();
    }
}