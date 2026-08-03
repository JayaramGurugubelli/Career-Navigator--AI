package career_Navigator_parent.learning.dto.response;

import career_Navigator_parent.learning.enums.AssessmentType;
import career_Navigator_parent.learning.enums.QuestionDifficulty;
import career_Navigator_parent.learning.enums.QuestionType;

import java.util.List;

public final class StudentAssessmentViewResponses {

    private StudentAssessmentViewResponses() {
    }

    public record Option(
            Long id,
            String optionText,
            String imageUrl,
            Integer sequenceNumber
    ) {
    }

    public record Question(
            Long id,
            String questionText,
            String questionContext,
            String imageUrl,
            QuestionType questionType,
            QuestionDifficulty difficulty,
            Integer sequenceNumber,
            Double marks,
            List<Option> options
    ) {
    }

    public record AssessmentView(
            Long id,
            String title,
            String description,
            String instructions,
            AssessmentType assessmentType,
            Double passingScore,
            Double maximumScore,
            Integer maximumAttempts,
            Integer durationMinutes,
            Boolean shuffleQuestions,
            Boolean shuffleOptions,
            Long courseId,
            Long milestoneId,
            List<Question> questions
    ) {
    }
}