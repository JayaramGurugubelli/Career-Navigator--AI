package careerpilot_parent.coding.service.impl;

import careerpilot_parent.coding.dto.response.StudentCodingResponses.*;
import careerpilot_parent.coding.entity.*;
import careerpilot_parent.coding.enums.*;
import careerpilot_parent.coding.repository.*;
import careerpilot_parent.coding.service.StudentCodingActivityService;
import careerpilot_parent.security.util.SecurityUtils;
import careerpilot_parent.student.entity.Student;
import careerpilot_parent.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentCodingActivityServiceImpl
        implements StudentCodingActivityService {

    private static final int MAX_RECOMMENDATION_LIMIT = 50;
    private static final int MAX_DATE_RANGE_YEARS = 5;

    private final CodingProblemRepository codingProblemRepository;
    private final ProblemAttemptRepository problemAttemptRepository;
    private final ProblemBookmarkRepository problemBookmarkRepository;
    private final CodeSubmissionRepository codeSubmissionRepository;
    private final StudentRepository studentRepository;
    private final SecurityUtils securityUtils;

    @Override
    public Dashboard dashboard() {
        Student student = getCurrentStudent();
        Long studentId = student.getId();

        long totalPublishedProblems =
                codingProblemRepository
                        .countByStatusAndActiveTrue(
                                ProblemStatus.PUBLISHED
                        );

        long attemptedProblems =
                problemAttemptRepository
                        .countByStudentId(studentId);

        long solvedProblems =
                problemAttemptRepository
                        .countByStudentIdAndStatus(
                                studentId,
                                ProblemAttemptStatus.SOLVED
                        );

        long totalSubmissions =
                codeSubmissionRepository
                        .countByStudentId(studentId);

        long acceptedSubmissions =
                codeSubmissionRepository
                        .countByStudentIdAndStatus(
                                studentId,
                                SubmissionStatus.ACCEPTED
                        );

        long bookmarkedProblems =
                problemBookmarkRepository
                        .countByStudentId(studentId);

        List<ProblemAttempt> allAttempts =
                problemAttemptRepository
                        .findAllWithProblemAndTags(studentId);

        StreakSummary streak =
                calculateStreak(allAttempts);

        List<RecentAttempt> recentAttempts =
                problemAttemptRepository
                        .findTop10ByStudentIdOrderByLastAttemptedAtDesc(
                                studentId
                        )
                        .stream()
                        .map(this::mapRecentAttempt)
                        .toList();

        return new Dashboard(
                totalPublishedProblems,
                attemptedProblems,
                solvedProblems,
                Math.max(
                        0,
                        attemptedProblems - solvedProblems
                ),
                bookmarkedProblems,
                totalSubmissions,
                acceptedSubmissions,
                percentage(
                        acceptedSubmissions,
                        totalSubmissions
                ),
                streak.currentStreak(),
                streak.longestStreak(),
                problemAttemptRepository
                        .findLastActivityAt(studentId),
                buildDifficultyProgress(allAttempts),
                buildTopicProgress(allAttempts),
                recentAttempts,
                recommendations(null, 5)
        );
    }

    @Override
    public OverallProgress progress(
            LocalDate fromDate,
            LocalDate toDate
    ) {
        Student student = getCurrentStudent();

        DateRange dateRange =
                resolveDateRange(
                        fromDate,
                        toDate
                );

        List<ProblemAttempt> attemptsInRange =
                problemAttemptRepository
                        .findStudentActivityBetween(
                                student.getId(),
                                dateRange.startInclusive(),
                                dateRange.endExclusive()
                        );

        long attemptedProblems =
                attemptsInRange.stream()
                        .map(attempt ->
                                attempt.getProblem().getId()
                        )
                        .distinct()
                        .count();

        long solvedProblems =
                attemptsInRange.stream()
                        .filter(attempt ->
                                attempt.getStatus()
                                        == ProblemAttemptStatus.SOLVED
                        )
                        .map(attempt ->
                                attempt.getProblem().getId()
                        )
                        .distinct()
                        .count();

        long totalPublishedProblems =
                codingProblemRepository
                        .countByStatusAndActiveTrue(
                                ProblemStatus.PUBLISHED
                        );

        long totalSubmissions =
                codeSubmissionRepository
                        .countStudentSubmissionsBetween(
                                student.getId(),
                                dateRange.startInclusive(),
                                dateRange.endExclusive()
                        );

        long acceptedSubmissions =
                codeSubmissionRepository
                        .countStudentSubmissionsByStatusBetween(
                                student.getId(),
                                SubmissionStatus.ACCEPTED,
                                dateRange.startInclusive(),
                                dateRange.endExclusive()
                        );

        StreakSummary streak =
                calculateStreak(attemptsInRange);

        LocalDateTime lastActivityAt =
                attemptsInRange.stream()
                        .map(ProblemAttempt::getLastAttemptedAt)
                        .filter(Objects::nonNull)
                        .max(LocalDateTime::compareTo)
                        .orElse(null);

        return new OverallProgress(
                dateRange.fromDate(),
                dateRange.toDate(),
                totalPublishedProblems,
                attemptedProblems,
                solvedProblems,
                Math.max(
                        0,
                        attemptedProblems - solvedProblems
                ),
                totalSubmissions,
                acceptedSubmissions,
                percentage(
                        solvedProblems,
                        totalPublishedProblems
                ),
                percentage(
                        acceptedSubmissions,
                        totalSubmissions
                ),
                streak.currentStreak(),
                streak.longestStreak(),
                lastActivityAt
        );
    }

    @Override
    public List<TopicProgress> topicProgress() {
        Student student = getCurrentStudent();

        List<ProblemAttempt> attempts =
                problemAttemptRepository
                        .findAllWithProblemAndTags(
                                student.getId()
                        );

        return buildTopicProgress(attempts);
    }

    @Override
    public List<DifficultyProgress>
    difficultyProgress() {
        Student student = getCurrentStudent();

        List<ProblemAttempt> attempts =
                problemAttemptRepository
                        .findAllWithProblemAndTags(
                                student.getId()
                        );

        return buildDifficultyProgress(attempts);
    }

    @Override
    public ActivityCalendar activityCalendar(
            LocalDate fromDate,
            LocalDate toDate
    ) {
        Student student = getCurrentStudent();

        DateRange range =
                resolveCalendarDateRange(
                        fromDate,
                        toDate
                );

        List<ProblemAttempt> attempts =
                problemAttemptRepository
                        .findStudentActivityBetween(
                                student.getId(),
                                range.startInclusive(),
                                range.endExclusive()
                        );

        Map<LocalDate, List<ProblemAttempt>> byDate =
                attempts.stream()
                        .filter(attempt ->
                                attempt.getLastAttemptedAt()
                                        != null
                        )
                        .collect(
                                Collectors.groupingBy(
                                        attempt ->
                                                attempt
                                                        .getLastAttemptedAt()
                                                        .toLocalDate()
                                )
                        );

        List<ActivityDay> days =
                new ArrayList<>();

        LocalDate date = range.fromDate();

        while (!date.isAfter(range.toDate())) {
            List<ProblemAttempt> dayAttempts =
                    byDate.getOrDefault(
                            date,
                            List.of()
                    );

            long solved =
                    dayAttempts.stream()
                            .filter(attempt ->
                                    attempt.getStatus()
                                            == ProblemAttemptStatus.SOLVED
                            )
                            .map(attempt ->
                                    attempt.getProblem().getId()
                            )
                            .distinct()
                            .count();

            long submissions =
                    dayAttempts.stream()
                            .mapToLong(attempt ->
                                    safeInteger(
                                            attempt
                                                    .getTotalSubmissionCount()
                                    )
                            )
                            .sum();

            long accepted =
                    dayAttempts.stream()
                            .mapToLong(attempt ->
                                    safeInteger(
                                            attempt
                                                    .getAcceptedSubmissionCount()
                                    )
                            )
                            .sum();

            days.add(
                    new ActivityDay(
                            date,
                            submissions,
                            accepted,
                            solved,
                            calculateActivityLevel(
                                    submissions
                            )
                    )
            );

            date = date.plusDays(1);
        }

        long activeDays =
                days.stream()
                        .filter(day ->
                                day.submissions() > 0
                                        || day.problemsSolved() > 0
                        )
                        .count();

        long totalSubmissions =
                days.stream()
                        .mapToLong(
                                ActivityDay::submissions
                        )
                        .sum();

        long totalSolved =
                days.stream()
                        .mapToLong(
                                ActivityDay::problemsSolved
                        )
                        .sum();

        return new ActivityCalendar(
                range.fromDate(),
                range.toDate(),
                activeDays,
                totalSubmissions,
                totalSolved,
                days
        );
    }

    @Override
    public List<Recommendation> recommendations(
            ProblemDifficulty difficulty,
            int limit
    ) {
        Student student = getCurrentStudent();

        int normalizedLimit =
                Math.min(
                        Math.max(limit, 1),
                        MAX_RECOMMENDATION_LIMIT
                );

        ProblemDifficulty preferredDifficulty =
                difficulty == null
                        ? resolvePreferredDifficulty(
                        student.getId()
                )
                        : difficulty;

        List<CodingProblem> problems =
                codingProblemRepository
                        .findRecommendations(
                                student.getId(),
                                ProblemStatus.PUBLISHED,
                                ProblemAttemptStatus.SOLVED,
                                difficulty,
                                preferredDifficulty,
                                PageRequest.of(
                                        0,
                                        normalizedLimit
                                )
                        );

        Set<Long> bookmarkedProblemIds =
                problemBookmarkRepository
                        .findByStudentIdOrderByCreatedAtDesc(
                                student.getId()
                        )
                        .stream()
                        .map(bookmark ->
                                bookmark.getProblem().getId()
                        )
                        .collect(Collectors.toSet());

        return problems.stream()
                .map(problem ->
                        new Recommendation(
                                problem.getId(),
                                problem.getTitle(),
                                problem.getSlug(),
                                problem.getDifficulty(),
                                problem.getTags()
                                        .stream()
                                        .filter(tag ->
                                                Boolean.TRUE.equals(
                                                        tag.getActive()
                                                )
                                        )
                                        .map(ProblemTag::getName)
                                        .sorted()
                                        .toList(),
                                recommendationReason(
                                        problem,
                                        preferredDifficulty
                                ),
                                recommendationScore(
                                        problem,
                                        preferredDifficulty
                                ),
                                bookmarkedProblemIds
                                        .contains(problem.getId())
                        )
                )
                .toList();
    }

    @Override
    public Page<Attempt> attempts(
            ProblemAttemptStatus status,
            Pageable pageable
    ) {
        Student student = getCurrentStudent();

        Pageable normalizedPageable =
                normalizePageable(pageable);

        return problemAttemptRepository
                .searchStudentAttempts(
                        student.getId(),
                        status,
                        normalizedPageable
                )
                .map(this::mapAttempt);
    }

    @Override
    @Transactional
    public Bookmark addBookmark(Long problemId) {
        Student student = getCurrentStudent();

        CodingProblem problem =
                getPublishedProblem(problemId);

        Optional<ProblemBookmark> existing =
                problemBookmarkRepository
                        .findByStudentIdAndProblemId(
                                student.getId(),
                                problemId
                        );

        if (existing.isPresent()) {
            ProblemBookmark bookmark =
                    existing.get();

            return new Bookmark(
                    problemId,
                    true,
                    bookmark.getCreatedAt()
            );
        }

        ProblemBookmark bookmark =
                ProblemBookmark.builder()
                        .student(student)
                        .problem(problem)
                        .build();

        ProblemBookmark saved =
                problemBookmarkRepository.save(
                        bookmark
                );

        return new Bookmark(
                problemId,
                true,
                saved.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public void removeBookmark(Long problemId) {
        Student student = getCurrentStudent();

        ProblemBookmark bookmark =
                problemBookmarkRepository
                        .findByStudentIdAndProblemId(
                                student.getId(),
                                problemId
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Problem bookmark not found."
                                )
                        );

        problemBookmarkRepository.delete(bookmark);
    }

    private List<DifficultyProgress>
    buildDifficultyProgress(
            List<ProblemAttempt> attempts
    ) {
        List<DifficultyProgress> result =
                new ArrayList<>();

        for (
                ProblemDifficulty difficulty
                : ProblemDifficulty.values()
        ) {
            long totalProblems =
                    codingProblemRepository
                            .countByStatusAndActiveTrueAndDifficulty(
                                    ProblemStatus.PUBLISHED,
                                    difficulty
                            );

            long attemptedProblems =
                    attempts.stream()
                            .filter(attempt ->
                                    attempt.getProblem()
                                            .getDifficulty()
                                            == difficulty
                            )
                            .map(attempt ->
                                    attempt.getProblem().getId()
                            )
                            .distinct()
                            .count();

            long solvedProblems =
                    attempts.stream()
                            .filter(attempt ->
                                    attempt.getProblem()
                                            .getDifficulty()
                                            == difficulty
                            )
                            .filter(attempt ->
                                    attempt.getStatus()
                                            == ProblemAttemptStatus.SOLVED
                            )
                            .map(attempt ->
                                    attempt.getProblem().getId()
                            )
                            .distinct()
                            .count();

            result.add(
                    new DifficultyProgress(
                            difficulty,
                            totalProblems,
                            attemptedProblems,
                            solvedProblems,
                            percentage(
                                    solvedProblems,
                                    totalProblems
                            )
                    )
            );
        }

        return result;
    }

    private List<TopicProgress> buildTopicProgress(
            List<ProblemAttempt> attempts
    ) {
        Map<Long, TopicAccumulator> topicMap =
                new HashMap<>();

        for (ProblemAttempt attempt : attempts) {
            CodingProblem problem =
                    attempt.getProblem();

            if (problem.getTags() == null) {
                continue;
            }

            for (ProblemTag tag : problem.getTags()) {
                TopicAccumulator accumulator =
                        topicMap.computeIfAbsent(
                                tag.getId(),
                                ignored ->
                                        new TopicAccumulator(
                                                tag.getId(),
                                                tag.getName(),
                                                tag.getSlug()
                                        )
                        );

                accumulator.attemptedProblemIds()
                        .add(problem.getId());

                if (
                        attempt.getStatus()
                                == ProblemAttemptStatus.SOLVED
                ) {
                    accumulator.solvedProblemIds()
                            .add(problem.getId());
                }
            }
        }

        return topicMap.values()
                .stream()
                .map(accumulator -> {
                    long attempted =
                            accumulator
                                    .attemptedProblemIds()
                                    .size();

                    long solved =
                            accumulator
                                    .solvedProblemIds()
                                    .size();

                    /*
                     * Until a tag-specific published-problem count
                     * query is added, attempted is used as the
                     * available topic total.
                     */
                    long total =
                            Math.max(attempted, solved);

                    return new TopicProgress(
                            accumulator.tagId(),
                            accumulator.tagName(),
                            accumulator.tagSlug(),
                            total,
                            attempted,
                            solved,
                            percentage(
                                    solved,
                                    total
                            )
                    );
                })
                .sorted(
                        Comparator.comparing(
                                TopicProgress::tagName,
                                String.CASE_INSENSITIVE_ORDER
                        )
                )
                .toList();
    }

    private Attempt mapAttempt(
            ProblemAttempt attempt
    ) {
        CodingProblem problem =
                attempt.getProblem();

        return new Attempt(
                attempt.getId(),
                problem.getId(),
                problem.getTitle(),
                problem.getSlug(),
                problem.getDifficulty(),
                attempt.getStatus(),
                attempt.getAttemptCount(),
                attempt.getAcceptedSubmissionCount(),
                attempt.getBestScore(),
                attempt.getFirstAttemptedAt(),
                attempt.getLastAttemptedAt(),
                attempt.getSolvedAt()
        );
    }

    private RecentAttempt mapRecentAttempt(
            ProblemAttempt attempt
    ) {
        CodingProblem problem =
                attempt.getProblem();

        return new RecentAttempt(
                problem.getId(),
                problem.getTitle(),
                problem.getSlug(),
                problem.getDifficulty(),
                attempt.getStatus(),
                attempt.getAttemptCount(),
                attempt.getLastAttemptedAt()
        );
    }

    private Student getCurrentStudent() {
        Long userId =
                securityUtils.getCurrentUserId();

        return studentRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Student profile not found."
                        )
                );
    }

    private CodingProblem getPublishedProblem(
            Long problemId
    ) {
        validateId(
                problemId,
                "A valid problem ID is required."
        );

        return codingProblemRepository
                .findByIdAndStatusAndActiveTrue(
                        problemId,
                        ProblemStatus.PUBLISHED
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Published coding problem not found."
                        )
                );
    }

    private DateRange resolveDateRange(
            LocalDate fromDate,
            LocalDate toDate
    ) {
        LocalDate resolvedTo =
                toDate == null
                        ? LocalDate.now()
                        : toDate;

        LocalDate resolvedFrom =
                fromDate == null
                        ? resolvedTo.minusDays(29)
                        : fromDate;

        validateDateRange(
                resolvedFrom,
                resolvedTo
        );

        return new DateRange(
                resolvedFrom,
                resolvedTo,
                resolvedFrom.atStartOfDay(),
                resolvedTo.plusDays(1)
                        .atStartOfDay()
        );
    }

    private DateRange resolveCalendarDateRange(
            LocalDate fromDate,
            LocalDate toDate
    ) {
        LocalDate resolvedTo =
                toDate == null
                        ? LocalDate.now()
                        : toDate;

        LocalDate resolvedFrom =
                fromDate == null
                        ? LocalDate.of(
                        resolvedTo.getYear(),
                        1,
                        1
                )
                        : fromDate;

        validateDateRange(
                resolvedFrom,
                resolvedTo
        );

        return new DateRange(
                resolvedFrom,
                resolvedTo,
                resolvedFrom.atStartOfDay(),
                resolvedTo.plusDays(1)
                        .atStartOfDay()
        );
    }

    private void validateDateRange(
            LocalDate fromDate,
            LocalDate toDate
    ) {
        if (fromDate.isAfter(toDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "fromDate cannot be after toDate."
            );
        }

        if (
                ChronoUnit.YEARS.between(
                        fromDate,
                        toDate
                ) > MAX_DATE_RANGE_YEARS
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Date range cannot exceed five years."
            );
        }
    }

    private Pageable normalizePageable(
            Pageable pageable
    ) {
        if (pageable == null) {
            return PageRequest.of(
                    0,
                    10,
                    Sort.by(
                            Sort.Direction.DESC,
                            "lastAttemptedAt"
                    )
            );
        }

        int safeSize =
                Math.min(
                        Math.max(
                                pageable.getPageSize(),
                                1
                        ),
                        100
                );

        Sort sort =
                pageable.getSort().isSorted()
                        ? pageable.getSort()
                        : Sort.by(
                        Sort.Direction.DESC,
                        "lastAttemptedAt"
                );

        return PageRequest.of(
                pageable.getPageNumber(),
                safeSize,
                sort
        );
    }

    private ProblemDifficulty resolvePreferredDifficulty(
            Long studentId
    ) {
        List<ProblemAttempt> attempts =
                problemAttemptRepository
                        .findAllWithProblemAndTags(
                                studentId
                        );

        long solvedEasy =
                solvedCount(
                        attempts,
                        ProblemDifficulty.EASY
                );

        long solvedMedium =
                solvedCount(
                        attempts,
                        ProblemDifficulty.MEDIUM
                );

        if (solvedMedium >= 10) {
            return ProblemDifficulty.HARD;
        }

        if (solvedEasy >= 5) {
            return ProblemDifficulty.MEDIUM;
        }

        return ProblemDifficulty.EASY;
    }

    private long solvedCount(
            List<ProblemAttempt> attempts,
            ProblemDifficulty difficulty
    ) {
        return attempts.stream()
                .filter(attempt ->
                        attempt.getStatus()
                                == ProblemAttemptStatus.SOLVED
                )
                .filter(attempt ->
                        attempt.getProblem()
                                .getDifficulty()
                                == difficulty
                )
                .count();
    }

    private String recommendationReason(
            CodingProblem problem,
            ProblemDifficulty preferredDifficulty
    ) {
        if (
                problem.getDifficulty()
                        == preferredDifficulty
        ) {
            return "Recommended for your current skill level.";
        }

        return "Recommended because you have not solved this problem yet.";
    }

    private double recommendationScore(
            CodingProblem problem,
            ProblemDifficulty preferredDifficulty
    ) {
        double score =
                problem.getDifficulty()
                        == preferredDifficulty
                        ? 90.0
                        : 75.0;

        Long totalSubmissions =
                problem.getTotalSubmissions();

        Long acceptedSubmissions =
                problem.getAcceptedSubmissions();

        if (
                totalSubmissions != null
                        && totalSubmissions > 0
                        && acceptedSubmissions != null
        ) {
            double acceptanceRate =
                    acceptedSubmissions
                            * 100.0
                            / totalSubmissions;

            score += Math.min(
                    acceptanceRate / 20.0,
                    5.0
            );
        }

        return Math.round(score * 100.0) / 100.0;
    }

    private StreakSummary calculateStreak(
            List<ProblemAttempt> attempts
    ) {
        SortedSet<LocalDate> activeDates =
                attempts.stream()
                        .map(
                                ProblemAttempt
                                        ::getLastAttemptedAt
                        )
                        .filter(Objects::nonNull)
                        .map(LocalDateTime::toLocalDate)
                        .collect(
                                Collectors.toCollection(
                                        TreeSet::new
                                )
                        );

        if (activeDates.isEmpty()) {
            return new StreakSummary(0, 0);
        }

        int longest = 0;
        int running = 0;
        LocalDate previous = null;

        for (LocalDate date : activeDates) {
            if (
                    previous != null
                            && previous.plusDays(1)
                            .equals(date)
            ) {
                running++;
            } else {
                running = 1;
            }

            longest = Math.max(longest, running);
            previous = date;
        }

        LocalDate today = LocalDate.now();
        LocalDate startingDate;

        if (activeDates.contains(today)) {
            startingDate = today;
        } else if (
                activeDates.contains(
                        today.minusDays(1)
                )
        ) {
            startingDate = today.minusDays(1);
        } else {
            return new StreakSummary(
                    0,
                    longest
            );
        }

        int current = 0;
        LocalDate cursor = startingDate;

        while (activeDates.contains(cursor)) {
            current++;
            cursor = cursor.minusDays(1);
        }

        return new StreakSummary(
                current,
                longest
        );
    }

    private int calculateActivityLevel(
            long submissions
    ) {
        if (submissions <= 0) {
            return 0;
        }

        if (submissions <= 2) {
            return 1;
        }

        if (submissions <= 5) {
            return 2;
        }

        if (submissions <= 9) {
            return 3;
        }

        return 4;
    }

    private double percentage(
            long numerator,
            long denominator
    ) {
        if (denominator <= 0) {
            return 0.0;
        }

        double value =
                numerator * 100.0 / denominator;

        return Math.round(value * 100.0) / 100.0;
    }

    private long safeInteger(Integer value) {
        return value == null ? 0 : value;
    }

    private void validateId(
            Long id,
            String message
    ) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    message
            );
        }
    }

    private record DateRange(
            LocalDate fromDate,
            LocalDate toDate,
            LocalDateTime startInclusive,
            LocalDateTime endExclusive
    ) {
    }

    private record StreakSummary(
            int currentStreak,
            int longestStreak
    ) {
    }

    private record TopicAccumulator(
            Long tagId,
            String tagName,
            String tagSlug,
            Set<Long> attemptedProblemIds,
            Set<Long> solvedProblemIds
    ) {
        private TopicAccumulator(
                Long tagId,
                String tagName,
                String tagSlug
        ) {
            this(
                    tagId,
                    tagName,
                    tagSlug,
                    new HashSet<>(),
                    new HashSet<>()
            );
        }
    }
}