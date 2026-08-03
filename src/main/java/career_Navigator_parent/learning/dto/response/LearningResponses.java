package career_Navigator_parent.learning.dto.response;

import career_Navigator_parent.learning.enums.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public final class LearningResponses {
    private LearningResponses() {}

    public record Discipline(Long id,String name,String code,String description,String iconUrl,
                             Integer displayOrder,Boolean active) {}
    public record Domain(Long id,String name,String slug,String description,String iconUrl,
                         Integer displayOrder,Boolean active) {}
    public record Role(Long id,Long domainId,String domainName,String title,String slug,String summary,
                       CareerDifficulty difficulty,String thumbnailUrl,Boolean featured,Set<Discipline> disciplines) {}
    public record Milestone(Long id,String title,String description,MilestoneType milestoneType,
                            Integer sequenceNumber,Boolean mandatory,Integer estimatedHours) {}
    public record PathSummary(Long id,Long careerRoleId,String careerRoleTitle,String title,String slug,
                              String description,LearningLevel level,Integer estimatedDurationHours,
                              ContentStatus status,Boolean premium,Boolean featured,Integer pathVersion,
                              Set<Discipline> disciplines) {}
    public record PathCourse(Long id,Long milestoneId,String milestoneTitle,Long courseId,String courseTitle,
                             Integer sequenceNumber,Boolean mandatory,UnlockRule unlockRule,Double minimumScore) {}
    public record PathDetail(PathSummary path,List<Milestone> milestones,List<PathCourse> courses,
                             boolean enrolled,Double progressPercentage) {}
    public record CourseSummary(Long id,String title,String slug,String description,CourseType courseType,
                                LearningLevel level,ProviderType providerType,String providerName,
                                Integer estimatedDurationHours,Boolean free,Boolean certificateEnabled,
                                ContentStatus status) {}
    public record Lesson(Long id,String title,LessonType lessonType,Integer durationMinutes,Integer sequenceNumber,
                         Boolean preview,ProgressStatus progressStatus,Double progressPercentage,
                         Long lastPositionSeconds) {}
    public record Module(Long id,String title,String description,Integer sequenceNumber,Integer estimatedMinutes,
                         Boolean mandatory,List<Lesson> lessons) {}
    public record CourseDetail(CourseSummary course,List<Module> modules) {}
    public record AssessmentAttempt(Long attemptId,Long assessmentId,String assessmentTitle,Integer attemptNumber,
                                    AssessmentAttemptStatus status,Double score,Double percentageScore,Boolean passed,
                                    Integer correctAnswers,Integer wrongAnswers,Integer unansweredQuestions,
                                    LocalDateTime startedAt,LocalDateTime expiresAt,LocalDateTime submittedAt,
                                    LocalDateTime evaluatedAt) {}
    public record Enrollment(Long enrollmentId,Long learningPathId,String learningPathTitle,EnrollmentStatus status,
                             Double progressPercentage,Integer completedCourses,Integer totalCourses,
                             Long currentMilestoneId,String currentMilestoneTitle,LocalDateTime enrolledAt,
                             LocalDateTime lastAccessedAt) {}
    public record WeeklyGoal(Long id,LocalDate weekStartDate,LocalDate weekEndDate,Integer targetMinutes,
                             Integer completedMinutes,Integer targetLessons,Integer completedLessons,
                             LearningGoalStatus status) {}
    public record Recommendation(Long learningPathId,String title,String careerRole,String reason,
                                 Double relevanceScore) {}
    public record Dashboard(Enrollment activeEnrollment,CourseSummary currentCourse,Lesson nextLesson,
                            WeeklyGoal weeklyGoal,long completedCourses,long completedLessons,long passedAssessments,
                            long approvedProjects,long certificateCount,List<CourseSummary> recentCourses,
                            List<Recommendation> recommendations) {}
}
