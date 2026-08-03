package career_Navigator_parent.learning.dto.response;

import career_Navigator_parent.learning.enums.AssessmentStatus;
import career_Navigator_parent.learning.enums.AssessmentType;
import career_Navigator_parent.learning.enums.ContentStatus;

public final class LearningPublicationResponses {

    private LearningPublicationResponses() {
    }

    public record CourseStatus(
            Long courseId,
            String title,
            String slug,
            ContentStatus status,
            Boolean active
    ) {
    }

    public record AssessmentStatusResponse(
            Long assessmentId,
            String title,
            AssessmentType assessmentType,
            AssessmentStatus status,
            Boolean active,
            Long courseId,
            Long milestoneId,
            long activeQuestionCount
    ) {
    }
}