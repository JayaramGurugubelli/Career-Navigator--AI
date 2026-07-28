package careerpilot_parent.analytics.service.impl;

import careerpilot_parent.analytics.dto.response.*;
import careerpilot_parent.analytics.projection.JobPerformanceProjection;
import careerpilot_parent.analytics.projection.SourcePerformanceProjection;
import careerpilot_parent.analytics.projection.StatusCountProjection;
import careerpilot_parent.analytics.repository.AnalyticsRepository;
import careerpilot_parent.analytics.service.RecruiterAnalyticsService;

import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.company.enums.JobStatus;
import careerpilot_parent.interview.enums.InterviewStatus;
import careerpilot_parent.offer.enums.OfferStatus;
import careerpilot_parent.company.entity.RecruiterProfile;
import careerpilot_parent.company.repository.RecruiterProfileRepository;
import careerpilot_parent.security.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruiterAnalyticsServiceImpl
        implements RecruiterAnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final SecurityUtils securityUtils;

    @Override
    public RecruiterDashboardResponse getDashboard(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        RecruiterProfile recruiter = getCurrentRecruiter();

        DateRange range = createRange(fromDate, toDate);

        long totalJobs = analyticsRepository.countJobs(
                recruiter.getId(),
                range.from(),
                range.toExclusive()
        );

        long activeJobs = analyticsRepository.countActiveJobs(
                recruiter.getId(),
                range.from(),
                range.toExclusive()
        );

        long closedJobs = analyticsRepository.countClosedJobs(
                recruiter.getId(),
                range.from(),
                range.toExclusive()
        );

        long draftJobs = analyticsRepository.countDraftJobs(
                recruiter.getId(),
                range.from(),
                range.toExclusive()
        );

        long totalApplications =
                analyticsRepository.countApplications(
                        recruiter.getId(),
                        range.from(),
                        range.toExclusive()
                );

        Map<String, Long> applicationStatusCounts =
                getApplicationStatusCounts(
                        recruiter.getId(),
                        null,
                        range
                );

        long underReview = countAny(
                applicationStatusCounts,
                "UNDER_REVIEW",
                "REVIEWING"
        );

        long shortlisted = countAny(
                applicationStatusCounts,
                "SHORTLISTED",
                "SHORTLIST"
        );

        long rejected = countAny(
                applicationStatusCounts,
                "REJECTED"
        );

        long withdrawn = countAny(
                applicationStatusCounts,
                "WITHDRAWN"
        );

        long scheduledInterviews = countInterviewStatusSafely(
                recruiter.getId(),
                null,
                "SCHEDULED",
                range
        );

        long completedInterviews = countInterviewStatusSafely(
                recruiter.getId(),
                null,
                "COMPLETED",
                range
        );

        long totalInterviews =
                analyticsRepository.countInterviews(
                        recruiter.getId(),
                        null,
                        range.from(),
                        range.toExclusive()
                );

        long offersSent = countOfferStatusSafely(
                recruiter.getId(),
                null,
                "SENT",
                range
        );

        long offersAccepted = countOfferStatusSafely(
                recruiter.getId(),
                null,
                "ACCEPTED",
                range
        );

        long offersRejected = countOfferStatusSafely(
                recruiter.getId(),
                null,
                "REJECTED",
                range
        );

        long totalOffers =
                analyticsRepository.countOffers(
                        recruiter.getId(),
                        null,
                        range.from(),
                        range.toExclusive()
                );

        return RecruiterDashboardResponse.builder()
                .totalJobs(totalJobs)
                .activeJobs(activeJobs)
                .closedJobs(closedJobs)
                .draftJobs(draftJobs)
                .totalApplications(totalApplications)
                .applicationsUnderReview(underReview)
                .shortlistedApplications(shortlisted)
                .rejectedApplications(rejected)
                .withdrawnApplications(withdrawn)
                .scheduledInterviews(scheduledInterviews)
                .completedInterviews(completedInterviews)
                .offersSent(offersSent)
                .offersAccepted(offersAccepted)
                .offersRejected(offersRejected)
                .candidatesHired(offersAccepted)
                .applicationToInterviewRate(
                        percentage(
                                totalInterviews,
                                totalApplications
                        )
                )
                .interviewToOfferRate(
                        percentage(totalOffers, totalInterviews)
                )
                .offerAcceptanceRate(
                        percentage(offersAccepted, totalOffers)
                )
                .build();
    }

    @Override
    public Page<JobAnalyticsResponse> getJobAnalytics(
            String keyword,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    ) {

        RecruiterProfile recruiter = getCurrentRecruiter();
        DateRange range = createRange(fromDate, toDate);

        String normalizedKeyword =
                normalizeKeyword(keyword);

        OfferStatus acceptedStatus =
                requiredOfferStatus("ACCEPTED");

        return analyticsRepository.findJobPerformance(
                        recruiter.getId(),
                        normalizedKeyword,
                        acceptedStatus,
                        range.from(),
                        range.toExclusive(),
                        pageable
                )
                .map(projection ->
                        mapJobAnalytics(
                                recruiter.getId(),
                                projection,
                                range
                        )
                );
    }

    @Override
    public JobAnalyticsResponse getJobAnalyticsById(
            Long jobId
    ) {

        RecruiterProfile recruiter = getCurrentRecruiter();

        OfferStatus acceptedStatus =
                requiredOfferStatus("ACCEPTED");

        JobPerformanceProjection projection =
                analyticsRepository.findJobPerformanceById(
                                recruiter.getId(),
                                jobId,
                                acceptedStatus
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job not found or does not belong " +
                                                "to the current recruiter."
                                )
                        );

        return mapJobAnalytics(
                recruiter.getId(),
                projection,
                new DateRange(null, null)
        );
    }

    @Override
    public ApplicationFunnelResponse getApplicationFunnel(
            Long jobId,
            LocalDate fromDate,
            LocalDate toDate
    ) {

        RecruiterProfile recruiter = getCurrentRecruiter();
        DateRange range = createRange(fromDate, toDate);

        Map<String, Long> counts =
                getApplicationStatusCounts(
                        recruiter.getId(),
                        jobId,
                        range
                );

        long total = counts.values()
                .stream()
                .mapToLong(Long::longValue)
                .sum();

        long applied = countAny(
                counts,
                "APPLIED",
                "SUBMITTED"
        );

        long underReview = countAny(
                counts,
                "UNDER_REVIEW",
                "REVIEWING"
        );

        long shortlisted = countAny(
                counts,
                "SHORTLISTED"
        );

        long interviewScheduled = countAny(
                counts,
                "INTERVIEW_SCHEDULED"
        );

        long interviewed = countAny(
                counts,
                "INTERVIEW_COMPLETED",
                "INTERVIEWED"
        );

        long offered = countAny(
                counts,
                "OFFERED",
                "OFFER_SENT"
        );

        long hired = countAny(
                counts,
                "HIRED",
                "SELECTED"
        );

        long rejected = countAny(
                counts,
                "REJECTED"
        );

        long withdrawn = countAny(
                counts,
                "WITHDRAWN"
        );

        /*
         * If application statuses are not automatically updated to
         * OFFERED or HIRED, use offer records as the source of truth.
         */
        if (offered == 0) {
            offered = analyticsRepository.countOffers(
                    recruiter.getId(),
                    jobId,
                    range.from(),
                    range.toExclusive()
            );
        }

        if (hired == 0) {
            hired = countOfferStatusSafely(
                    recruiter.getId(),
                    jobId,
                    "ACCEPTED",
                    range
            );
        }

        return ApplicationFunnelResponse.builder()
                .totalApplications(total)
                .applied(applied)
                .underReview(underReview)
                .shortlisted(shortlisted)
                .interviewScheduled(interviewScheduled)
                .interviewed(interviewed)
                .offered(offered)
                .hired(hired)
                .rejected(rejected)
                .withdrawn(withdrawn)
                .build();
    }

    @Override
    public List<HiringTrendResponse> getHiringTrends(
            int months
    ) {

        if (months < 1 || months > 24) {
            throw new IllegalArgumentException(
                    "Months must be between 1 and 24."
            );
        }

        RecruiterProfile recruiter = getCurrentRecruiter();

        YearMonth firstMonth =
                YearMonth.now().minusMonths(months - 1L);

        List<HiringTrendResponse> trends =
                new ArrayList<>();

        for (int index = 0; index < months; index++) {

            YearMonth currentMonth =
                    firstMonth.plusMonths(index);

            LocalDateTime from =
                    currentMonth.atDay(1).atStartOfDay();

            LocalDateTime toExclusive =
                    currentMonth.plusMonths(1)
                            .atDay(1)
                            .atStartOfDay();

            long applications =
                    analyticsRepository.countApplications(
                            recruiter.getId(),
                            from,
                            toExclusive
                    );

            long interviews =
                    analyticsRepository.countInterviews(
                            recruiter.getId(),
                            null,
                            from,
                            toExclusive
                    );

            long offers =
                    analyticsRepository.countOffers(
                            recruiter.getId(),
                            null,
                            from,
                            toExclusive
                    );

            long hires =
                    analyticsRepository.countOffersByStatus(
                            recruiter.getId(),
                            null,
                            requiredOfferStatus("ACCEPTED"),
                            from,
                            toExclusive
                    );

            trends.add(
                    HiringTrendResponse.builder()
                            .period(currentMonth.toString())
                            .applications(applications)
                            .interviews(interviews)
                            .offers(offers)
                            .hires(hires)
                            .build()
            );
        }

        return trends;
    }

    @Override
    public List<SourceAnalyticsResponse> getSourceAnalytics(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        RecruiterProfile recruiter = getCurrentRecruiter();
        DateRange range = createRange(fromDate, toDate);

        List<SourcePerformanceProjection> projections =
                analyticsRepository.findSourcePerformance(
                        recruiter.getId(),
                        requiredOfferStatus("ACCEPTED"),
                        range.from(),
                        range.toExclusive()
                );

        return projections.stream()
                .map(projection -> {

                    long applications =
                            safeLong(
                                    projection.getApplicationCount()
                            );

                    long interviews =
                            safeLong(projection.getInterviewCount());

                    long hires =
                            safeLong(projection.getHireCount());

                    return SourceAnalyticsResponse.builder()
                            .source(projection.getSource())
                            .applicationCount(applications)
                            .interviewCount(interviews)
                            .offerCount(
                                    safeLong(
                                            projection.getOfferCount()
                                    )
                            )
                            .hireCount(hires)
                            .interviewConversionRate(
                                    percentage(
                                            interviews,
                                            applications
                                    )
                            )
                            .hireConversionRate(
                                    percentage(
                                            hires,
                                            applications
                                    )
                            )
                            .build();
                })
                .toList();
    }

    @Override
    public RecruiterPerformanceResponse
    getRecruiterPerformance(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        RecruiterProfile recruiter = getCurrentRecruiter();
        DateRange range = createRange(fromDate, toDate);

        long jobs = analyticsRepository.countJobs(
                recruiter.getId(),
                range.from(),
                range.toExclusive()
        );

        long applications =
                analyticsRepository.countApplications(
                        recruiter.getId(),
                        range.from(),
                        range.toExclusive()
                );

        long interviews =
                analyticsRepository.countInterviews(
                        recruiter.getId(),
                        null,
                        range.from(),
                        range.toExclusive()
                );

        long offers =
                analyticsRepository.countOffers(
                        recruiter.getId(),
                        null,
                        range.from(),
                        range.toExclusive()
                );

        long hires =
                analyticsRepository.countOffersByStatus(
                        recruiter.getId(),
                        null,
                        requiredOfferStatus("ACCEPTED"),
                        range.from(),
                        range.toExclusive()
                );

        String recruiterName = buildRecruiterName(recruiter);

        String companyName =
                recruiter.getCompany() == null
                        ? null
                        : recruiter.getCompany().getName();

        return RecruiterPerformanceResponse.builder()
                .recruiterId(recruiter.getId())
                .recruiterName(recruiterName)
                .companyName(companyName)
                .totalJobs(jobs)
                .totalApplications(applications)
                .totalInterviews(interviews)
                .totalOffers(offers)
                .totalHires(hires)
                .applicationToInterviewRate(
                        percentage(interviews, applications)
                )
                .interviewToOfferRate(
                        percentage(offers, interviews)
                )
                .offerAcceptanceRate(
                        percentage(hires, offers)
                )
                .applicationToHireRate(
                        percentage(hires, applications)
                )
                .averageApplicationsPerJob(
                        average(applications, jobs)
                )
                .averageHiresPerJob(
                        average(hires, jobs)
                )
                .build();
    }

    private JobAnalyticsResponse mapJobAnalytics(
            Long recruiterId,
            JobPerformanceProjection projection,
            DateRange range
    ) {

        Long jobId = projection.getJobId();

        Map<String, Long> statusCounts =
                getApplicationStatusCounts(
                        recruiterId,
                        jobId,
                        range
                );

        long totalApplications =
                safeLong(projection.getTotalApplications());

        long interviews =
                safeLong(projection.getInterviewCount());

        long offers =
                safeLong(projection.getOfferCount());

        long hires =
                safeLong(projection.getHireCount());

        return JobAnalyticsResponse.builder()
                .jobId(jobId)
                .jobTitle(projection.getJobTitle())
                .status(parseJobStatus(projection.getStatus()))
                .totalApplications(totalApplications)
                .applicationsUnderReview(
                        countAny(
                                statusCounts,
                                "UNDER_REVIEW",
                                "REVIEWING"
                        )
                )
                .shortlistedApplications(
                        countAny(
                                statusCounts,
                                "SHORTLISTED"
                        )
                )
                .rejectedApplications(
                        countAny(statusCounts, "REJECTED")
                )
                .withdrawnApplications(
                        countAny(statusCounts, "WITHDRAWN")
                )
                .scheduledInterviews(
                        countInterviewStatusSafely(
                                recruiterId,
                                jobId,
                                "SCHEDULED",
                                range
                        )
                )
                .completedInterviews(
                        countInterviewStatusSafely(
                                recruiterId,
                                jobId,
                                "COMPLETED",
                                range
                        )
                )
                .offersSent(
                        countOfferStatusSafely(
                                recruiterId,
                                jobId,
                                "SENT",
                                range
                        )
                )
                .offersAccepted(hires)
                .candidatesHired(hires)
                .applicationToInterviewRate(
                        percentage(
                                interviews,
                                totalApplications
                        )
                )
                .interviewToOfferRate(
                        percentage(offers, interviews)
                )
                .offerAcceptanceRate(
                        percentage(hires, offers)
                )
                .applicationToHireRate(
                        percentage(
                                hires,
                                totalApplications
                        )
                )
                .publishedAt(projection.getPublishedAt())
                .closedAt(projection.getClosedAt())
                .build();
    }

    private Map<String, Long> getApplicationStatusCounts(
            Long recruiterId,
            Long jobId,
            DateRange range
    ) {

        return analyticsRepository
                .countApplicationsByStatus(
                        recruiterId,
                        jobId,
                        range.from(),
                        range.toExclusive()
                )
                .stream()
                .collect(
                        Collectors.toMap(
                                projection ->
                                        normalizeStatus(
                                                projection.getStatus()
                                        ),
                                projection ->
                                        safeLong(
                                                projection.getTotal()
                                        ),
                                Long::sum
                        )
                );
    }

    private RecruiterProfile getCurrentRecruiter() {

        Long userId = securityUtils.getCurrentUserId();

        return recruiterProfileRepository
                .findByUserIdAndActiveTrue(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active recruiter profile not found."
                        )
                );
    }

    private DateRange createRange(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        if (fromDate != null
                && toDate != null
                && fromDate.isAfter(toDate)) {

            throw new IllegalArgumentException(
                    "fromDate cannot be after toDate."
            );
        }

        LocalDateTime from =
                fromDate == null
                        ? null
                        : fromDate.atStartOfDay();

        /*
         * Exclusive upper boundary includes the entire toDate.
         */
        LocalDateTime toExclusive =
                toDate == null
                        ? null
                        : toDate.plusDays(1).atStartOfDay();

        return new DateRange(from, toExclusive);
    }

    private long countInterviewStatusSafely(
            Long recruiterId,
            Long jobId,
            String statusName,
            DateRange range
    ) {

        Optional<InterviewStatus> status =
                enumValue(
                        InterviewStatus.class,
                        statusName
                );

        if (status.isEmpty()) {
            return 0L;
        }

        return analyticsRepository.countInterviewsByStatus(
                recruiterId,
                jobId,
                status.get(),
                range.from(),
                range.toExclusive()
        );
    }

    private long countOfferStatusSafely(
            Long recruiterId,
            Long jobId,
            String statusName,
            DateRange range
    ) {

        Optional<OfferStatus> status =
                enumValue(
                        OfferStatus.class,
                        statusName
                );

        if (status.isEmpty()) {
            return 0L;
        }

        return analyticsRepository.countOffersByStatus(
                recruiterId,
                jobId,
                status.get(),
                range.from(),
                range.toExclusive()
        );
    }

    private OfferStatus requiredOfferStatus(
            String statusName
    ) {

        return enumValue(OfferStatus.class, statusName)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "OfferStatus." + statusName +
                                        " is required for analytics."
                        )
                );
    }

    private JobStatus parseJobStatus(String value) {

        if (value == null) {
            return null;
        }

        return enumValue(JobStatus.class, value)
                .orElse(null);
    }

    private <E extends Enum<E>> Optional<E> enumValue(
            Class<E> enumClass,
            String value
    ) {

        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(
                    Enum.valueOf(
                            enumClass,
                            normalizeStatus(value)
                    )
            );
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    private long countAny(
            Map<String, Long> counts,
            String... names
    ) {

        long total = 0L;

        for (String name : names) {
            total += counts.getOrDefault(
                    normalizeStatus(name),
                    0L
            );
        }

        return total;
    }

    private String normalizeStatus(String status) {

        if (status == null) {
            return "";
        }

        String normalized = status.trim();

        int lastDot = normalized.lastIndexOf('.');

        if (lastDot >= 0) {
            normalized = normalized.substring(lastDot + 1);
        }

        return normalized
                .replace(' ', '_')
                .replace('-', '_')
                .toUpperCase(Locale.ROOT);
    }

    private String normalizeKeyword(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return keyword.trim();
    }

    private double percentage(
            long numerator,
            long denominator
    ) {

        if (denominator <= 0) {
            return 0.0;
        }

        return BigDecimal.valueOf(numerator)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(denominator),
                        2,
                        RoundingMode.HALF_UP
                )
                .doubleValue();
    }

    private double average(
            long total,
            long denominator
    ) {

        if (denominator <= 0) {
            return 0.0;
        }

        return BigDecimal.valueOf(total)
                .divide(
                        BigDecimal.valueOf(denominator),
                        2,
                        RoundingMode.HALF_UP
                )
                .doubleValue();
    }

    private long safeLong(Long value) {
        return value == null ? 0L : value;
    }

    private String buildRecruiterName(
            RecruiterProfile recruiter
    ) {

        if (recruiter.getUser() == null) {
            return null;
        }

        String firstName =
                Optional.ofNullable(
                                recruiter.getUser().getFirstName()
                        )
                        .orElse("");

        String lastName =
                Optional.ofNullable(
                                recruiter.getUser().getLastName()
                        )
                        .orElse("");

        return (firstName + " " + lastName).trim();
    }

    private record DateRange(
            LocalDateTime from,
            LocalDateTime toExclusive
    ) {
    }
}