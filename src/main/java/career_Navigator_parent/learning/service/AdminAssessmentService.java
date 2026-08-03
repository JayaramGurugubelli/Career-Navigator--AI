package career_Navigator_parent.learning.service;

import career_Navigator_parent.learning.dto.request.AdminLearningRequests;
import career_Navigator_parent.learning.dto.request.LearningStatusRequests;
import career_Navigator_parent.learning.dto.response.LearningPublicationResponses;

public interface AdminAssessmentService {

    Long create(
            AdminLearningRequests.AssessmentCreate request
    );

    LearningPublicationResponses.AssessmentStatusResponse updateStatus(
            Long assessmentId,
            LearningStatusRequests.AssessmentStatusUpdate request
    );
}