package career_Navigator_parent.learning.service;

import career_Navigator_parent.learning.dto.request.StudentLearningRequests;
import career_Navigator_parent.learning.dto.response.LearningResponses;

public interface StudentLearningAssessmentService {

    LearningResponses.AssessmentAttempt start(
            Long assessmentId
    );

    LearningResponses.AssessmentAttempt submit(
            Long attemptId,
            StudentLearningRequests.SubmitAssessment request
    );

    LearningResponses.AssessmentAttempt get(
            Long attemptId
    );
}