package career_Navigator_parent.learning.mapper;

import career_Navigator_parent.learning.dto.response.LearningResponses;
import career_Navigator_parent.learning.entity.*;
import career_Navigator_parent.learning.enums.ProgressStatus;
import org.springframework.stereotype.Component;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LearningMapper {
    public LearningResponses.Discipline toDiscipline(AcademicDiscipline e) {
        return new LearningResponses.Discipline(e.getId(),e.getName(),e.getCode(),e.getDescription(),
                e.getIconUrl(),e.getDisplayOrder(),e.getActive());
    }
    public LearningResponses.Domain toDomain(CareerDomain e) {
        return new LearningResponses.Domain(e.getId(),e.getName(),e.getSlug(),e.getDescription(),
                e.getIconUrl(),e.getDisplayOrder(),e.getActive());
    }
    public LearningResponses.Role toRole(CareerRole e) {
        Set<LearningResponses.Discipline> ds=e.getEligibleDisciplines().stream()
                .map(this::toDiscipline).collect(Collectors.toCollection(LinkedHashSet::new));
        return new LearningResponses.Role(e.getId(),e.getDomain().getId(),e.getDomain().getName(),
                e.getTitle(),e.getSlug(),e.getSummary(),e.getDifficulty(),e.getThumbnailUrl(),
                e.getFeatured(),ds);
    }
    public LearningResponses.PathSummary toPath(LearningPath e) {
        Set<LearningResponses.Discipline> ds=e.getDisciplines().stream()
                .map(this::toDiscipline).collect(Collectors.toCollection(LinkedHashSet::new));
        return new LearningResponses.PathSummary(e.getId(),e.getCareerRole().getId(),
                e.getCareerRole().getTitle(),e.getTitle(),e.getSlug(),e.getDescription(),e.getLevel(),
                e.getEstimatedDurationHours(),e.getStatus(),e.getPremium(),e.getFeatured(),
                e.getPathVersion(),ds);
    }
    public LearningResponses.CourseSummary toCourse(Course e) {
        return new LearningResponses.CourseSummary(e.getId(),e.getTitle(),e.getSlug(),e.getDescription(),
                e.getCourseType(),e.getLevel(),e.getProviderType(),e.getProviderName(),
                e.getEstimatedDurationHours(),e.getFree(),e.getCertificateEnabled(),e.getStatus());
    }
    public LearningResponses.Lesson toLesson(Lesson e,StudentLessonProgress p) {
        return new LearningResponses.Lesson(e.getId(),e.getTitle(),e.getLessonType(),
                e.getDurationMinutes(),e.getSequenceNumber(),e.getPreview(),
                p==null? ProgressStatus.NOT_STARTED:p.getStatus(),
                p==null?0.0:p.getProgressPercentage(),p==null?0L:p.getLastPositionSeconds());
    }
    public LearningResponses.Enrollment toEnrollment(StudentLearningPathEnrollment e) {
        return new LearningResponses.Enrollment(e.getId(),e.getLearningPath().getId(),
                e.getLearningPath().getTitle(),e.getStatus(),e.getProgressPercentage(),
                e.getCompletedCourses(),e.getTotalCourses(),
                e.getCurrentMilestone()==null?null:e.getCurrentMilestone().getId(),
                e.getCurrentMilestone()==null?null:e.getCurrentMilestone().getTitle(),
                e.getEnrolledAt(),e.getLastAccessedAt());
    }
    public LearningResponses.AssessmentAttempt toAttempt(StudentAssessmentAttempt e) {
        return new LearningResponses.AssessmentAttempt(e.getId(),e.getAssessment().getId(),
                e.getAssessment().getTitle(),e.getAttemptNumber(),e.getStatus(),e.getScore(),
                e.getPercentageScore(),e.getPassed(),e.getCorrectAnswers(),e.getWrongAnswers(),
                e.getUnansweredQuestions(),e.getStartedAt(),e.getExpiresAt(),e.getSubmittedAt(),
                e.getEvaluatedAt());
    }
}
