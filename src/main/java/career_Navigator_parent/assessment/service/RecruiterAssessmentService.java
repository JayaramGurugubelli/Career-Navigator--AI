package career_Navigator_parent.assessment.service;

import career_Navigator_parent.assessment.dto.request.CreateAssessmentRequest;
import career_Navigator_parent.assessment.dto.request.UpdateAssessmentRequest;
import career_Navigator_parent.assessment.dto.request.UpdateAssessmentResultRequest;
import career_Navigator_parent.assessment.dto.request.UpdateAssessmentStatusRequest;
import career_Navigator_parent.assessment.dto.response.AssessmentResponse;
import career_Navigator_parent.assessment.enums.AssessmentStatus;
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