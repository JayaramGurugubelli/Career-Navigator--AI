package careerpilot_parent.assessment.service;

import careerpilot_parent.assessment.dto.response.AssessmentResponse;
import careerpilot_parent.assessment.enums.AssessmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentAssessmentService {

    Page<AssessmentResponse> getMyAssessments(
            AssessmentStatus status,
            Pageable pageable
    );

    AssessmentResponse getAssessmentById(
            Long assessmentId
    );

    AssessmentResponse startAssessment(
            Long assessmentId
    );

    AssessmentResponse submitAssessment(
            Long assessmentId
    );
}