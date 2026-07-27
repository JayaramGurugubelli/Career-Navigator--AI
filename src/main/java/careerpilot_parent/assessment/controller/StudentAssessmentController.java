package careerpilot_parent.assessment.controller;

import careerpilot_parent.assessment.dto.response.AssessmentResponse;
import careerpilot_parent.assessment.enums.AssessmentStatus;
import careerpilot_parent.assessment.service.StudentAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/assessments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentAssessmentController {

    private final StudentAssessmentService
            studentAssessmentService;

    @GetMapping
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
                studentAssessmentService
                        .getMyAssessments(
                                status,
                                pageable
                        )
        );
    }

    @GetMapping("/{assessmentId}")
    public ResponseEntity<AssessmentResponse>
    getAssessmentById(
            @PathVariable Long assessmentId
    ) {

        return ResponseEntity.ok(
                studentAssessmentService
                        .getAssessmentById(
                                assessmentId
                        )
        );
    }

    @PatchMapping("/{assessmentId}/start")
    public ResponseEntity<AssessmentResponse>
    startAssessment(
            @PathVariable Long assessmentId
    ) {

        return ResponseEntity.ok(
                studentAssessmentService
                        .startAssessment(
                                assessmentId
                        )
        );
    }

    @PatchMapping("/{assessmentId}/submit")
    public ResponseEntity<AssessmentResponse>
    submitAssessment(
            @PathVariable Long assessmentId
    ) {

        return ResponseEntity.ok(
                studentAssessmentService
                        .submitAssessment(
                                assessmentId
                        )
        );
    }
}