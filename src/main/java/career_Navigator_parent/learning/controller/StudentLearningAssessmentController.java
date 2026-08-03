package career_Navigator_parent.learning.controller;

import career_Navigator_parent.learning.dto.request.StudentLearningRequests;
import career_Navigator_parent.learning.dto.response.LearningResponses;
import career_Navigator_parent.learning.service.StudentLearningAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/learning")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentLearningAssessmentController {

    private final StudentLearningAssessmentService
            studentLearningAssessmentService;

    @PostMapping(
            value = "/assessments/{assessmentId}/attempts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<
            LearningResponses.AssessmentAttempt
            > startAttempt(
            @PathVariable Long assessmentId
    ) {
        LearningResponses.AssessmentAttempt response =
                studentLearningAssessmentService.start(
                        assessmentId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping(
            value = "/attempts/{attemptId}/submit",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<
            LearningResponses.AssessmentAttempt
            > submitAttempt(
            @PathVariable Long attemptId,

            @Valid
            @RequestBody
            StudentLearningRequests.SubmitAssessment request
    ) {
        LearningResponses.AssessmentAttempt response =
                studentLearningAssessmentService.submit(
                        attemptId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            value = "/attempts/{attemptId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<
            LearningResponses.AssessmentAttempt
            > getAttempt(
            @PathVariable Long attemptId
    ) {
        LearningResponses.AssessmentAttempt response =
                studentLearningAssessmentService.get(
                        attemptId
                );

        return ResponseEntity.ok(response);
    }
}