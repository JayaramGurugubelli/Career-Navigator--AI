package career_Navigator_parent.learning.service;

import career_Navigator_parent.learning.dto.response.StudentAssessmentViewResponses;

public interface StudentAssessmentCatalogService {

    StudentAssessmentViewResponses.AssessmentView getPublishedAssessment(
            Long assessmentId
    );
}