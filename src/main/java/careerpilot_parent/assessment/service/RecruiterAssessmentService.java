package careerpilot_parent.assessment.service;

import careerpilot_parent.assessment.dto.request.CreateAssessmentRequest;
import careerpilot_parent.assessment.dto.request.UpdateAssessmentRequest;
import careerpilot_parent.assessment.dto.request.UpdateAssessmentResultRequest;
import careerpilot_parent.assessment.dto.request.UpdateAssessmentStatusRequest;
import careerpilot_parent.assessment.dto.response.AssessmentResponse;
import careerpilot_parent.assessment.enums.AssessmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruiterAssessmentService {

    AssessmentResponse createAssessment(
            Long applicationId,
            CreateAssessmentRequest request
    );

    Page<AssessmentResponse> getMyAssessments(
            AssessmentStatus status,
            Pageable pageable
    );

    Page<AssessmentResponse> getAssessmentsForApplication(
            Long applicationId,
            Pageable pageable
    );

    AssessmentResponse getAssessmentById(
            Long assessmentId
    );

    AssessmentResponse updateAssessment(
            Long assessmentId,
            UpdateAssessmentRequest request
    );

    AssessmentResponse updateAssessmentStatus(
            Long assessmentId,
            UpdateAssessmentStatusRequest request
    );

    AssessmentResponse updateAssessmentResult(
            Long assessmentId,
            UpdateAssessmentResultRequest request
    );

    void deleteAssessment(
            Long assessmentId
    );
}