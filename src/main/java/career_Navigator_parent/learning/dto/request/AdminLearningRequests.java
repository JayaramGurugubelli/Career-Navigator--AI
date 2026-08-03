package career_Navigator_parent.learning.dto.request;

import career_Navigator_parent.learning.enums.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public final class AdminLearningRequests {
    private AdminLearningRequests() {}

    public record DisciplineCreate(@NotBlank String name,@NotBlank String code,String description,
                                   String iconUrl,@PositiveOrZero Integer displayOrder,Boolean active) {}

    public record DomainCreate(@NotBlank String name,@NotBlank String slug,String description,
                               String iconUrl,@PositiveOrZero Integer displayOrder,Boolean active) {}

    public record RoleCreate(@NotNull Long domainId,@NotBlank String title,@NotBlank String slug,
                             @NotBlank String summary,String responsibilities,String workEnvironment,
                             String entryLevelTitles,String careerOutlook,String minimumQualification,
                             @PositiveOrZero Integer averageLearningMonths,@NotNull CareerDifficulty difficulty,
                             String thumbnailUrl,Boolean featured,Boolean active,@NotEmpty Set<Long> disciplineIds) {}

    public record PathCreate(@NotNull Long careerRoleId,@NotBlank String title,@NotBlank String slug,
                             @NotBlank String description,@NotNull LearningLevel level,
                             @Positive Integer estimatedDurationHours,String thumbnailUrl,Boolean premium,
                             Boolean featured,Boolean active,@Positive Integer pathVersion,
                             @NotEmpty Set<Long> disciplineIds) {}

    public record MilestoneCreate(@NotBlank String title,String description,@NotNull MilestoneType milestoneType,
                                  @Positive Integer sequenceNumber,Boolean mandatory,@Positive Integer estimatedHours) {}

    public record CourseCreate(@NotBlank String title,@NotBlank String slug,@NotBlank String description,
                               String learningOutcomes,String prerequisiteDescription,@NotNull CourseType courseType,
                               @NotNull LearningLevel level,@NotNull ProviderType providerType,String providerName,
                               String instructorName,String externalCourseUrl,String thumbnailUrl,String language,
                               @Positive Integer estimatedDurationHours,Boolean certificateEnabled,Boolean free,
                               Boolean featured,Boolean active,@Positive Integer courseVersion,
                               Set<Long> disciplineIds,Set<Long> prerequisiteCourseIds) {}

    public record ModuleCreate(@NotBlank String title,String description,@Positive Integer sequenceNumber,
                               @Positive Integer estimatedMinutes,Boolean mandatory,Boolean previewEnabled,
                               @Min(1) @Max(100) Integer completionPercentageRequired) {}

    public record LessonCreate(@NotBlank String title,String summary,@NotNull LessonType lessonType,String content,
                               String videoUrl,String externalUrl,String fileUrl,@Positive Integer durationMinutes,
                               @Positive Integer sequenceNumber,Boolean preview,Boolean completionRequired) {}

    public record PathCourseCreate(@NotNull Long milestoneId,@NotNull Long courseId,@Positive Integer sequenceNumber,
                                   Boolean mandatory,UnlockRule unlockRule,@DecimalMin("0.0") @DecimalMax("100.0")
                                   Double minimumScore,LocalDateTime scheduledReleaseAt,
                                   @Positive Integer estimatedHoursOverride) {}

    public record OptionCreate(@NotBlank String optionText,String imageUrl,@Positive Integer sequenceNumber,
                               Boolean correctOption) {}

    public record QuestionCreate(@NotBlank String questionText,String questionContext,String imageUrl,
                                 @NotNull QuestionType questionType,QuestionDifficulty difficulty,
                                 @Positive Integer sequenceNumber,@Positive Double marks,
                                 @PositiveOrZero Double negativeMarks,String expectedAnswer,String answerExplanation,
                                 Boolean caseSensitive,@PositiveOrZero Double numericTolerance,
                                 List<@Valid OptionCreate> options) {}

    public record AssessmentCreate(Long courseId,Long milestoneId,@NotBlank String title,String description,
                                   String instructions,@NotNull AssessmentType assessmentType,
                                   @DecimalMin("0.0") Double passingScore,@Positive Double maximumScore,
                                   @Positive Integer maximumAttempts,@Positive Integer durationMinutes,
                                   Boolean shuffleQuestions,Boolean shuffleOptions,Boolean showResultImmediately,
                                   Boolean showCorrectAnswers,Boolean negativeMarkingEnabled,
                                   @PositiveOrZero Double negativeMarksPerWrongAnswer,Boolean mandatory,
                                   Boolean active,List<@Valid QuestionCreate> questions) {
        @AssertTrue(message = "Assessment must belong to exactly one course or milestone.")
        public boolean hasOneOwner() { return (courseId == null) != (milestoneId == null); }
    }

    public record PathStatusUpdate(@NotNull ContentStatus status) {}
    public record ProjectReview(@NotNull @DecimalMin("0.0") Double score,String feedback) {}
}
