package career_Navigator_parent.learning.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;
import java.util.Set;

public final class StudentLearningRequests {

    private StudentLearningRequests() {
    }

    public record LessonProgress(

            @DecimalMin(
                    value = "0.0",
                    message = "Progress percentage cannot be less than 0."
            )
            @DecimalMax(
                    value = "100.0",
                    message = "Progress percentage cannot exceed 100."
            )
            double progressPercentage,

            @PositiveOrZero(
                    message = "Last position cannot be negative."
            )
            long lastPositionSeconds,

            @PositiveOrZero(
                    message = "Additional time cannot be negative."
            )
            long additionalTimeSeconds
    ) {
    }

    public record WeeklyGoal(

            @PositiveOrZero(
                    message = "Target minutes cannot be negative."
            )
            Integer targetMinutes,

            @PositiveOrZero(
                    message = "Target lessons cannot be negative."
            )
            Integer targetLessons
    ) {

        public WeeklyGoal {
            if (targetMinutes == null && targetLessons == null) {
                throw new IllegalArgumentException(
                        "At least one weekly goal target is required."
                );
            }
        }
    }

    public record AssessmentAnswer(

            @NotNull(
                    message = "Question ID is required."
            )
            Long questionId,

            Set<Long> selectedOptionIds,

            String textAnswer,

            Double numericAnswer,

            String fileUrl
    ) {
    }

    public record SubmitAssessment(

            @NotEmpty(
                    message = "At least one assessment answer is required."
            )
            List<
                    @NotNull(
                            message = "Assessment answer cannot be null."
                    )
                    @Valid AssessmentAnswer
                    > answers
    ) {
    }

    public record ProjectSubmission(

            String submissionText,

            String repositoryUrl,

            String externalUrl,

            String fileUrl,

            String videoUrl
    ) {
    }
}