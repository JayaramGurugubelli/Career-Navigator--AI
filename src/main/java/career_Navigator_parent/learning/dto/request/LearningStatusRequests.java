package career_Navigator_parent.learning.dto.request;

import career_Navigator_parent.learning.enums.AssessmentStatus;
import career_Navigator_parent.learning.enums.ContentStatus;
import jakarta.validation.constraints.NotNull;

public final class LearningStatusRequests {

    private LearningStatusRequests() {
    }

    public record CourseStatusUpdate(
            @NotNull ContentStatus status
    ) {
    }

    public record AssessmentStatusUpdate(
            @NotNull AssessmentStatus status
    ) {
    }
}
