package careerpilot_parent.coding.dto.response;

import careerpilot_parent.coding.enums.ProblemAttemptStatus;
import careerpilot_parent.coding.enums.ProblemDifficulty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public final class StudentCodingResponses {

    private StudentCodingResponses() {
    }

    public record Dashboard(
            long totalPublishedProblems,
            long attemptedProblems,
            long solvedProblems,
            long unsolvedAttemptedProblems,
            long bookmarkedProblems,
            long totalSubmissions,
            long acceptedSubmissions,
            double acceptanceRate,
            int currentStreak,
            int longestStreak,
            LocalDateTime lastActivityAt,
            List<DifficultyProgress> difficultyProgress,
            List<TopicProgress> topicProgress,
            List<RecentAttempt> recentAttempts,
            List<Recommendation> recommendations
    ) {
    }

    public record OverallProgress(
            LocalDate fromDate,
            LocalDate toDate,
            long totalPublishedProblems,
            long attemptedProblems,
            long solvedProblems,
            long unsolvedAttemptedProblems,
            long totalSubmissions,
            long acceptedSubmissions,
            double completionPercentage,
            double acceptanceRate,
            int currentStreak,
            int longestStreak,
            LocalDateTime lastActivityAt
    ) {
    }

    public record TopicProgress(
            Long tagId,
            String tagName,
            String tagSlug,
            long totalProblems,
            long attemptedProblems,
            long solvedProblems,
            double completionPercentage
    ) {
    }

    public record DifficultyProgress(
            ProblemDifficulty difficulty,
            long totalProblems,
            long attemptedProblems,
            long solvedProblems,
            double completionPercentage
    ) {
    }

    public record ActivityDay(
            LocalDate date,
            long submissions,
            long acceptedSubmissions,
            long problemsSolved,
            int activityLevel
    ) {
    }

    public record ActivityCalendar(
            LocalDate fromDate,
            LocalDate toDate,
            long activeDays,
            long totalSubmissions,
            long totalProblemsSolved,
            List<ActivityDay> days
    ) {
    }

    public record Attempt(
            Long attemptId,
            Long problemId,
            String problemTitle,
            String problemSlug,
            ProblemDifficulty difficulty,
            ProblemAttemptStatus status,
            Integer attemptCount,
            Integer acceptedSubmissionCount,
            Integer bestScore,
            LocalDateTime firstAttemptedAt,
            LocalDateTime lastAttemptedAt,
            LocalDateTime solvedAt
    ) {
    }

    public record RecentAttempt(
            Long problemId,
            String title,
            String slug,
            ProblemDifficulty difficulty,
            ProblemAttemptStatus status,
            Integer attemptCount,
            LocalDateTime lastAttemptedAt
    ) {
    }

    public record Recommendation(
            Long problemId,
            String title,
            String slug,
            ProblemDifficulty difficulty,
            List<String> tags,
            String reason,
            double score,
            boolean bookmarked
    ) {
    }

    public record Bookmark(
            Long problemId,
            boolean bookmarked,
            LocalDateTime bookmarkedAt
    ) {
    }

    public record ReviewHelpful(
            Long reviewId,
            boolean helpful,
            long helpfulCount
    ) {
    }
}