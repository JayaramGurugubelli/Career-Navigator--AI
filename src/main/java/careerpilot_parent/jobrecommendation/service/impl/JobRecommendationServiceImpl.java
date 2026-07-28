package careerpilot_parent.jobrecommendation.service.impl;

import careerpilot_parent.audit.annotation.Auditable;
import careerpilot_parent.audit.enums.AuditAction;
import careerpilot_parent.audit.enums.AuditEntityType;
import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.company.enums.JobStatus;
import careerpilot_parent.job.entity.JobPosting;
import careerpilot_parent.jobrecommendation.dto.response.JobRecommendationResponse;
import careerpilot_parent.jobrecommendation.entity.JobRecommendation;
import careerpilot_parent.jobrecommendation.enums.RecommendationSource;
import careerpilot_parent.jobrecommendation.mapper.JobRecommendationMapper;
import careerpilot_parent.jobrecommendation.repository.JobRecommendationRepository;
import careerpilot_parent.jobrecommendation.service.JobRecommendationService;
import careerpilot_parent.security.util.SecurityUtils;
import careerpilot_parent.student.entity.Student;
import careerpilot_parent.student.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobRecommendationServiceImpl implements JobRecommendationService {

    private static final double DEFAULT_MINIMUM_SCORE =
            20.0;

    private static final double MINIMUM_PERSISTED_SCORE =
            15.0;

    private static final int CANDIDATE_BATCH_SIZE =
            100;

    private static final int MAX_CANDIDATE_PAGES =
            10;

    private static final int SAVED_JOB_SIGNAL_LIMIT =
            20;

    private static final int RECOMMENDATION_VALIDITY_HOURS =
            24;

    private static final int ACTIVE_STUDENT_BATCH_SIZE =
            100;

    private final JobRecommendationRepository
            jobRecommendationRepository;

    private final StudentRepository
            studentRepository;

    private final JobRecommendationMapper
            jobRecommendationMapper;

    private final SecurityUtils
            securityUtils;

    @Override
    @Transactional(readOnly = true)
    public Page<JobRecommendationResponse>
    getMyRecommendations(Double minimumScore, RecommendationSource source, Pageable pageable) {

        Student student = getCurrentStudent();

        double normalizedMinimumScore = normalizeMinimumScore(minimumScore);

        Page<JobRecommendation> recommendations = jobRecommendationRepository
                        .findStudentRecommendations(
                                student.getId(),
                                normalizedMinimumScore,
                                source,
                                resolvePublishedStatus(),
                                LocalDate.now(),
                                LocalDateTime.now(),
                                pageable
                        );

        List<Long> jobIds =
                recommendations
                        .getContent()
                        .stream()
                        .map(recommendation ->
                                recommendation
                                        .getJobPosting()
                                        .getId()
                        )
                        .toList();

        Set<Long> savedJobIds =
                findSavedJobIds(
                        student.getId(),
                        jobIds
                );

        Set<Long> appliedJobIds =
                findAppliedJobIds(
                        student.getId(),
                        jobIds
                );

        return recommendations.map(
                recommendation -> {

                    Long jobId =
                            recommendation
                                    .getJobPosting()
                                    .getId();

                    return jobRecommendationMapper
                            .toResponse(
                                    recommendation,
                                    savedJobIds.contains(jobId),
                                    appliedJobIds.contains(jobId)
                            );
                }
        );
    }

    @Override
    @Transactional(readOnly = true)
    public JobRecommendationResponse
    getRecommendationByJobId(
            Long jobId
    ) {

        validatePositiveId(
                jobId,
                "Job ID"
        );

        Student student =
                getCurrentStudent();

        JobRecommendation recommendation =
                jobRecommendationRepository
                        .findActiveStudentRecommendation(
                                student.getId(),
                                jobId,
                                LocalDateTime.now()
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Active job recommendation not found."
                                        )
                        );

        Set<Long> savedJobIds =
                findSavedJobIds(
                        student.getId(),
                        List.of(jobId)
                );

        Set<Long> appliedJobIds =
                findAppliedJobIds(
                        student.getId(),
                        List.of(jobId)
                );

        return jobRecommendationMapper.toResponse(
                recommendation,
                savedJobIds.contains(jobId),
                appliedJobIds.contains(jobId)
        );
    }

    @Override
    @Transactional
    @Auditable(
            action = AuditAction.RECOMMENDATIONS_REFRESHED,
            entityType = AuditEntityType.RECOMMENDATION,
            description = "Student refreshed job recommendations"
    )
    public int refreshMyRecommendations() {

        Long userId =
                securityUtils.getCurrentUserId();

        Student student =
                studentRepository
                        .findByUserId(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Student profile not found."
                                )
                        );

        return refreshRecommendationsForStudent(
                student.getId()
        );
    }

    @Override
    @Transactional
    public int refreshRecommendationsForStudent(
            Long studentId
    ) {

        validatePositiveId(
                studentId,
                "Student ID"
        );

        Student student =
                jobRecommendationRepository
                        .findStudentWithExperiences(studentId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Student profile not found."
                                        )
                        );

        return generateRecommendations(student);
    }

    @Override
    public int refreshAllActiveStudents() {

        int pageNumber = 0;
        int refreshedStudents = 0;

        Page<Long> studentPage;

        do {
            studentPage =
                    jobRecommendationRepository
                            .findActiveStudentIds(
                                    PageRequest.of(
                                            pageNumber,
                                            ACTIVE_STUDENT_BATCH_SIZE
                                    )
                            );

            for (Long studentId :
                    studentPage.getContent()) {

                try {
                    refreshRecommendationsForStudent(
                            studentId
                    );

                    refreshedStudents++;

                } catch (RuntimeException exception) {

                    System.err.println(
                            "Recommendation refresh failed "
                                    + "for student ID "
                                    + studentId
                                    + ": "
                                    + exception.getMessage()
                    );
                }
            }

            pageNumber++;

        } while (studentPage.hasNext());

        return refreshedStudents;
    }

    @Override
    @Transactional
    public void dismissRecommendation(
            Long jobId
    ) {

        validatePositiveId(
                jobId,
                "Job ID"
        );

        Student student =
                getCurrentStudent();

        JobRecommendation recommendation =
                jobRecommendationRepository
                        .findByStudentIdAndJobPostingIdAndActiveTrue(
                                student.getId(),
                                jobId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Active job recommendation not found."
                                        )
                        );

        recommendation.setActive(false);
        recommendation.setDismissedAt(
                LocalDateTime.now()
        );

        jobRecommendationRepository.save(
                recommendation
        );
    }

    @Override
    @Transactional
    public int deactivateExpiredRecommendations() {

        return jobRecommendationRepository
                .deactivateExpiredRecommendations(
                        LocalDateTime.now()
                );
    }

    private int generateRecommendations(
            Student student
    ) {

        /*
         * Build every lazy-dependent profile signal before
         * executing the bulk update.
         */
        StudentProfileSignals profileSignals =
                buildStudentSignals(student);

        List<JobPosting> savedJobs =
                jobRecommendationRepository
                        .findRecentlySavedJobs(
                                student.getId(),
                                PageRequest.of(
                                        0,
                                        SAVED_JOB_SIGNAL_LIMIT
                                )
                        );

        Set<String> savedJobTokens =
                buildSavedJobTokens(savedJobs);

        /*
         * This update no longer clears the persistence context.
         */
        jobRecommendationRepository
                .deactivateStudentRecommendations(
                        student.getId()
                );

        List<JobRecommendation>
                generatedRecommendations =
                new ArrayList<>();

        int pageNumber = 0;
        boolean moreCandidates = true;

        while (moreCandidates
                && pageNumber < MAX_CANDIDATE_PAGES) {

            Page<JobPosting> candidatePage =
                    jobRecommendationRepository
                            .findRecommendationCandidates(
                                    student.getId(),
                                    resolvePublishedStatus(),
                                    LocalDate.now(),
                                    PageRequest.of(
                                            pageNumber,
                                            CANDIDATE_BATCH_SIZE
                                    )
                            );

            for (JobPosting job :
                    candidatePage.getContent()) {

                RecommendationCalculation calculation =
                        calculateRecommendation(
                                student,
                                profileSignals,
                                savedJobTokens,
                                job
                        );

                if (calculation.score()
                        < MINIMUM_PERSISTED_SCORE) {

                    continue;
                }

                JobRecommendation recommendation =
                        jobRecommendationRepository
                                .findByStudentIdAndJobPostingId(
                                        student.getId(),
                                        job.getId()
                                )
                                .orElseGet(
                                        JobRecommendation::new
                                );

                recommendation.setStudent(student);
                recommendation.setJobPosting(job);

                recommendation.setMatchScore(
                        calculation.score()
                );

                recommendation.setMatchedSkills(
                        new ArrayList<>(
                                calculation.matchedSkills()
                        )
                );

                recommendation.setMissingSkills(
                        new ArrayList<>(
                                calculation.missingSkills()
                        )
                );

                recommendation.setReasons(
                        new ArrayList<>(
                                calculation.reasons()
                        )
                );

                recommendation.setSource(
                        calculation.source()
                );

                LocalDateTime generatedAt =
                        LocalDateTime.now();

                recommendation.setGeneratedAt(
                        generatedAt
                );

                recommendation.setExpiresAt(
                        generatedAt.plusHours(
                                RECOMMENDATION_VALIDITY_HOURS
                        )
                );

                recommendation.setActive(true);
                recommendation.setDismissedAt(null);

                generatedRecommendations.add(
                        recommendation
                );
            }

            moreCandidates =
                    candidatePage.hasNext();

            pageNumber++;
        }

        if (!generatedRecommendations.isEmpty()) {

            jobRecommendationRepository.saveAll(
                    generatedRecommendations
            );

            jobRecommendationRepository.flush();
        }

        return generatedRecommendations.size();
    }

    private RecommendationCalculation
    calculateRecommendation(
            Student student,
            StudentProfileSignals profile,
            Set<String> savedJobTokens,
            JobPosting job
    ) {

        double score = 0.0;

        List<String> reasons =
                new ArrayList<>();

        Set<String> studentSkills =
                profile.skills();

        Set<String> requiredSkills =
                normalizeSkills(
                        job.getRequiredSkills()
                );

        Set<String> matchedSkills =
                new TreeSet<>(
                        String.CASE_INSENSITIVE_ORDER
                );

        Set<String> missingSkills =
                new TreeSet<>(
                        String.CASE_INSENSITIVE_ORDER
                );

        if (!requiredSkills.isEmpty()) {

            for (String requiredSkill :
                    requiredSkills) {

                if (containsSkill(
                        studentSkills,
                        requiredSkill
                )) {

                    matchedSkills.add(
                            requiredSkill
                    );

                } else {

                    missingSkills.add(
                            requiredSkill
                    );
                }
            }

            double skillRatio =
                    (double) matchedSkills.size()
                            / requiredSkills.size();

            double skillScore =
                    skillRatio * 55.0;

            score += skillScore;

            if (!matchedSkills.isEmpty()) {

                reasons.add(
                        "Your profile matches "
                                + matchedSkills.size()
                                + " of "
                                + requiredSkills.size()
                                + " required skills."
                );
            }

        } else {

            score += 15.0;

            reasons.add(
                    "This job does not specify restrictive required skills."
            );
        }

        double experienceScore =
                calculateExperienceScore(
                        profile.experienceYears(),
                        job
                );

        score += experienceScore;

        if (experienceScore >= 10.0) {

            reasons.add(
                    "Your experience level matches the job requirements."
            );
        }

        double educationScore =
                calculateEducationScore(
                        student,
                        job
                );

        score += educationScore;

        if (educationScore > 0.0) {

            reasons.add(
                    "Your degree or branch is relevant to this job."
            );
        }

        double savedBehaviourScore =
                calculateSavedBehaviourScore(
                        savedJobTokens,
                        job
                );

        score += savedBehaviourScore;

        if (savedBehaviourScore > 0.0) {

            reasons.add(
                    "This job is similar to jobs you previously saved."
            );
        }

        double recencyScore =
                calculateRecencyScore(job);

        score += recencyScore;

        if (recencyScore >= 7.0) {

            reasons.add(
                    "This is a recently published job."
            );
        }

        if (reasons.isEmpty()) {

            reasons.add(
                    "This role has partial relevance to your student profile."
            );
        }

        RecommendationSource source =
                resolveSource(
                        !matchedSkills.isEmpty(),
                        savedBehaviourScore > 0.0,
                        experienceScore > 0.0
                );

        return new RecommendationCalculation(
                roundScore(score),
                new ArrayList<>(matchedSkills),
                new ArrayList<>(missingSkills),
                reasons,
                source
        );
    }

    private StudentProfileSignals buildStudentSignals(
            Student student
    ) {

        Set<String> skills =
                jobRecommendationRepository
                        .findStudentSkillNames(
                                student.getId()
                        )
                        .stream()
                        .filter(Objects::nonNull)
                        .map(this::normalizeToken)
                        .filter(value -> !value.isBlank())
                        .collect(Collectors.toSet());

        if (student.getBranch() != null) {

            skills.addAll(
                    tokenize(student.getBranch())
            );
        }

        if (student.getDegree() != null) {

            skills.addAll(
                    tokenize(student.getDegree())
            );
        }

        double experienceYears = 0.0;

        if (student.getExperiences() != null
                && !student.getExperiences().isEmpty()) {

            long totalMonths = 0;

            for (var experience :
                    student.getExperiences()) {

                if (experience.getStartDate() == null) {
                    continue;
                }

                LocalDate endDate =
                        Boolean.TRUE.equals(
                                experience.getCurrentlyWorking()
                        )
                                ? LocalDate.now()
                                : experience.getEndDate();

                if (endDate == null
                        || endDate.isBefore(
                        experience.getStartDate()
                )) {

                    continue;
                }

                totalMonths +=
                        ChronoUnit.MONTHS.between(
                                experience.getStartDate(),
                                endDate
                        );

                if (experience.getJobTitle() != null) {

                    skills.addAll(
                            tokenize(
                                    experience.getJobTitle()
                            )
                    );
                }

                if (experience.getTechnologies() != null) {

                    skills.addAll(
                            tokenize(
                                    experience
                                            .getTechnologies()
                                            .toString()
                            )
                    );
                }
            }

            experienceYears =
                    totalMonths / 12.0;
        }

        return new StudentProfileSignals(
                skills,
                experienceYears
        );
    }

    private Set<String> buildSavedJobTokens(
            List<JobPosting> savedJobs
    ) {

        Set<String> tokens =
                new HashSet<>();

        for (JobPosting job : savedJobs) {

            tokens.addAll(
                    tokenize(job.getTitle())
            );

            tokens.addAll(
                    normalizeSkills(
                            job.getRequiredSkills()
                    )
            );

            if (job.getExperienceLevel() != null) {

                tokens.add(
                        normalizeToken(
                                job.getExperienceLevel()
                                        .name()
                        )
                );
            }

            if (job.getEmploymentType() != null) {

                tokens.add(
                        normalizeToken(
                                job.getEmploymentType()
                                        .name()
                        )
                );
            }
        }

        return tokens;
    }

    private double calculateExperienceScore(
            double studentExperienceYears,
            JobPosting job
    ) {

        Integer minimum =
                job.getMinimumExperience();

        Integer maximum =
                job.getMaximumExperience();

        if (minimum == null && maximum == null) {
            return 8.0;
        }

        double lowerBound =
                minimum == null
                        ? 0.0
                        : minimum;

        double upperBound =
                maximum == null
                        ? Double.MAX_VALUE
                        : maximum;

        if (studentExperienceYears >= lowerBound
                && studentExperienceYears <= upperBound) {

            return 15.0;
        }

        if (studentExperienceYears < lowerBound) {

            double gap =
                    lowerBound
                            - studentExperienceYears;

            if (gap <= 1.0) {
                return 9.0;
            }

            if (gap <= 2.0) {
                return 4.0;
            }

            return 0.0;
        }

        return 10.0;
    }

    private double calculateEducationScore(
            Student student,
            JobPosting job
    ) {

        Set<String> studentTokens =
                new HashSet<>();

        studentTokens.addAll(
                tokenize(student.getDegree())
        );

        studentTokens.addAll(
                tokenize(student.getBranch())
        );

        Set<String> jobTokens =
                new HashSet<>();

        jobTokens.addAll(
                tokenize(job.getTitle())
        );

        jobTokens.addAll(
                tokenize(job.getDescription())
        );

        if (studentTokens.isEmpty()
                || jobTokens.isEmpty()) {

            return 0.0;
        }

        boolean matched =
                studentTokens.stream()
                        .anyMatch(jobTokens::contains);

        return matched ? 10.0 : 0.0;
    }

    private double calculateSavedBehaviourScore(
            Set<String> savedJobTokens,
            JobPosting job
    ) {

        if (savedJobTokens.isEmpty()) {
            return 0.0;
        }

        Set<String> candidateTokens =
                new HashSet<>();

        candidateTokens.addAll(
                tokenize(job.getTitle())
        );

        candidateTokens.addAll(
                normalizeSkills(
                        job.getRequiredSkills()
                )
        );

        if (candidateTokens.isEmpty()) {
            return 0.0;
        }

        long matchedTokens =
                candidateTokens.stream()
                        .filter(savedJobTokens::contains)
                        .count();

        double ratio =
                (double) matchedTokens
                        / candidateTokens.size();

        return Math.min(
                10.0,
                ratio * 10.0
        );
    }

    private double calculateRecencyScore(
            JobPosting job
    ) {

        if (job.getPublishedAt() == null) {
            return 2.0;
        }

        long ageInDays =
                ChronoUnit.DAYS.between(
                        job.getPublishedAt()
                                .toLocalDate(),
                        LocalDate.now()
                );

        if (ageInDays <= 3) {
            return 10.0;
        }

        if (ageInDays <= 7) {
            return 8.0;
        }

        if (ageInDays <= 14) {
            return 6.0;
        }

        if (ageInDays <= 30) {
            return 4.0;
        }

        return 2.0;
    }

    private RecommendationSource resolveSource(
            boolean skillMatched,
            boolean savedBehaviourMatched,
            boolean experienceMatched
    ) {

        int matchedSignals = 0;

        if (skillMatched) {
            matchedSignals++;
        }

        if (savedBehaviourMatched) {
            matchedSignals++;
        }

        if (experienceMatched) {
            matchedSignals++;
        }

        if (matchedSignals >= 2) {

            return RecommendationSource
                    .HYBRID_RULE_BASED;
        }

        if (savedBehaviourMatched) {

            return RecommendationSource
                    .SAVED_JOB_BEHAVIOR;
        }

        if (experienceMatched) {

            return RecommendationSource
                    .EXPERIENCE_MATCH;
        }

        return RecommendationSource
                .PROFILE_SKILLS;
    }

    private Set<Long> findSavedJobIds(
            Long studentId,
            List<Long> jobIds
    ) {

        if (jobIds == null || jobIds.isEmpty()) {
            return Set.of();
        }

        return new HashSet<>(
                jobRecommendationRepository
                        .findSavedJobIds(
                                studentId,
                                jobIds
                        )
        );
    }

    private Set<Long> findAppliedJobIds(
            Long studentId,
            List<Long> jobIds
    ) {

        if (jobIds == null || jobIds.isEmpty()) {
            return Set.of();
        }

        return new HashSet<>(
                jobRecommendationRepository
                        .findAppliedJobIds(
                                studentId,
                                jobIds
                        )
        );
    }

    private boolean containsSkill(
            Set<String> studentSkills,
            String requiredSkill
    ) {

        String normalizedRequired =
                normalizeToken(requiredSkill);

        return studentSkills.stream()
                .anyMatch(studentSkill ->
                        studentSkill.equals(
                                normalizedRequired
                        )
                                || studentSkill.contains(
                                normalizedRequired
                        )
                                || normalizedRequired.contains(
                                studentSkill
                        )
                );
    }

    private Set<String> normalizeSkills(
            Collection<String> skills
    ) {

        if (skills == null) {
            return new HashSet<>();
        }

        return skills.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toSet());
    }

    private Set<String> tokenize(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return new HashSet<>();
        }

        return Arrays.stream(
                        value.toLowerCase(Locale.ROOT)
                                .replaceAll(
                                        "[^a-z0-9+#.]+",
                                        " "
                                )
                                .trim()
                                .split("\\s+")
                )
                .filter(token ->
                        token.length() >= 2
                )
                .filter(token ->
                        !STOP_WORDS.contains(token)
                )
                .collect(Collectors.toSet());
    }

    private String normalizeToken(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

    private double normalizeMinimumScore(
            Double minimumScore
    ) {

        if (minimumScore == null) {
            return DEFAULT_MINIMUM_SCORE;
        }

        if (minimumScore < 0.0
                || minimumScore > 100.0) {

            throw new IllegalArgumentException(
                    "Minimum score must be between 0 and 100."
            );
        }

        return minimumScore;
    }

    private double roundScore(
            double score
    ) {

        double boundedScore =
                Math.max(
                        0.0,
                        Math.min(
                                100.0,
                                score
                        )
                );

        return BigDecimal.valueOf(
                        boundedScore
                )
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                )
                .doubleValue();
    }

    private JobStatus resolvePublishedStatus() {

        for (String statusName :
                List.of(
                        "PUBLISHED",
                        "OPEN",
                        "ACTIVE"
                )) {

            try {

                return JobStatus.valueOf(
                        statusName
                );

            } catch (IllegalArgumentException ignored) {
            }
        }

        throw new IllegalStateException(
                "JobStatus enum must contain "
                        + "PUBLISHED, OPEN, or ACTIVE."
        );
    }

    private Student getCurrentStudent() {

        Long userId =
                securityUtils.getCurrentUserId();

        return studentRepository
                .findByUserId(userId)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Student profile not found."
                                )
                );
    }

    private void validatePositiveId(
            Long id,
            String fieldName
    ) {

        if (id == null || id <= 0) {

            throw new IllegalArgumentException(
                    fieldName
                            + " must be positive."
            );
        }
    }

    private static final Set<String>
            STOP_WORDS =
            Set.of(
                    "and",
                    "or",
                    "the",
                    "a",
                    "an",
                    "to",
                    "for",
                    "of",
                    "in",
                    "on",
                    "with",
                    "is",
                    "are",
                    "as",
                    "at",
                    "be",
                    "this",
                    "that"
            );

    private record StudentProfileSignals(
            Set<String> skills,
            double experienceYears
    ) {
    }

    private record RecommendationCalculation(
            double score,
            List<String> matchedSkills,
            List<String> missingSkills,
            List<String> reasons,
            RecommendationSource source
    ) {
    }

}