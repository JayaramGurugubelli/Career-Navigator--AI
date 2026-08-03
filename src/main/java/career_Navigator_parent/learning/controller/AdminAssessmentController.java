package career_Navigator_parent.learning.controller;

import career_Navigator_parent.learning.dto.request.AdminLearningRequests;
import career_Navigator_parent.learning.dto.request.LearningStatusRequests;
import career_Navigator_parent.learning.dto.response.LearningPublicationResponses;
import career_Navigator_parent.learning.service.AdminAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/learning/assessments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAssessmentController {

    private final AdminAssessmentService adminAssessmentService;

    @PostMapping
    public ResponseEntity<Map<String, Long>> create(
            @Valid
            @RequestBody
            AdminLearningRequests.AssessmentCreate request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        Map.of(
                                "assessmentId",
                                adminAssessmentService
                                        .create(request)
                        )
                );
    }

    @PatchMapping("/{assessmentId}/status")
    public ResponseEntity<
            LearningPublicationResponses.AssessmentStatusResponse
            > updateStatus(
            @PathVariable Long assessmentId,
            @Valid
            @RequestBody
            LearningStatusRequests.AssessmentStatusUpdate request
    ) {
        return ResponseEntity.ok(
                adminAssessmentService.updateStatus(
                        assessmentId,
                        request
                )
        );
    }
}
