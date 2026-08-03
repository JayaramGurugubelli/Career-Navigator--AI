package career_Navigator_parent.learning.controller;

import career_Navigator_parent.learning.dto.response.StudentAssessmentViewResponses;
import career_Navigator_parent.learning.service.StudentAssessmentCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/learning/assessments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentAssessmentCatalogController {

    private final StudentAssessmentCatalogService assessmentCatalogService;

    @GetMapping("/{assessmentId}")
    public ResponseEntity<
            StudentAssessmentViewResponses.AssessmentView
            > getAssessment(
            @PathVariable Long assessmentId
    ) {
        return ResponseEntity.ok(
                assessmentCatalogService
                        .getPublishedAssessment(
                                assessmentId
                        )
        );
    }
}
