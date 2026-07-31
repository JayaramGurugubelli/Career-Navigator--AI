package career_Navigator_parent.jobtracker.service.impl;

import career_Navigator_parent.job.dto.response.ApplicationStatusHistoryResponse;
import career_Navigator_parent.job.entity.ApplicationStatusHistory;
import career_Navigator_parent.job.entity.JobApplication;
import career_Navigator_parent.job.service.ApplicationStatusHistoryService;
import career_Navigator_parent.shared.enums.ApplicationStatus;

import career_Navigator_parent.common.exception.ResourceNotFoundException;

import career_Navigator_parent.interview.entity.Interview;
import career_Navigator_parent.interview.enums.InterviewStatus;

import career_Navigator_parent.jobtracker.dto.response.*;

import career_Navigator_parent.jobtracker.projection.StudentApplicationTrackerProjection;
import career_Navigator_parent.jobtracker.repository.StudentJobTrackerRepository;
import career_Navigator_parent.jobtracker.service.StudentJobTrackerService;

import career_Navigator_parent.offer.entity.JobOffer;
import career_Navigator_parent.offer.enums.OfferStatus;

import career_Navigator_parent.security.util.SecurityUtils;

import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentJobTrackerServiceImpl
        implements StudentJobTrackerService {

    private final StudentJobTrackerRepository
            studentJobTrackerRepository;

    private final StudentRepository studentRepository;

    private final ApplicationStatusHistoryService
            applicationStatusHistoryService;

    private final SecurityUtils securityUtils;

    @Override
    public JobTrackerDashboardResponse getDashboard(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        Student student = getCurrentStudent();

        DateRange range =
                createDateRange(fromDate, toDate);

        long totalApplications =
                studentJobTrackerRepository.countApplications(
                        student.getId(),
                        range.from(),
                        range.toExclusive()
                );

        Map<String, Long> statusCounts =
                studentJobTrackerRepository
                        .countApplicationsByStatus(
                                student.getId(),
                                range.from(),
                                range.toExclusive()
                        )
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        projection ->
                                                normalizeStatus(
                                                        projection
                                                                .getStatus()
                                                ),
                                        projection ->
                                                safeLong(
                                                        projection
                                                                .getTotal()
                                                ),
                                        Long::sum
                                )
                        );

        long applied = countAny(
                statusCounts,
                "APPLIED",
                "SUBMITTED"
        );

        long underReview = countAny(
                statusCounts,
                "UNDER_REVIEW",
                "REVIEWING"
        );

        long shortlisted = countAny(
                statusCounts,
                "SHORTLISTED",
                "SHORTLIST"
        );

        long interviewScheduled = countAny(
                statusCounts,
                "INTERVIEW_SCHEDULED"
        );

        long interviewCompleted = countAny(
                statusCounts,
                "INTERVIEW_COMPLETED",
                "INTERVIEWED"
        );

        long offered = countAny(
                statusCounts,
                "OFFERED",
                "OFFER_SENT"
        );

        long hired = countAny(
                statusCounts,
                "HIRED",
                "SELECTED"
        );

        long rejected = countAny(
                statusCounts,
                "REJECTED"
        );

        long withdrawn = countAny(
                statusCounts,
                "WITHDRAWN"
        );

        List<InterviewStatus> activeInterviewStatuses =
                resolveInterviewStatuses(
                        "SCHEDULED",
                        "RESCHEDULED",
                        "CONFIRMED"
                );

        long upcomingInterviews =
                activeInterviewStatuses.isEmpty()
                        ? 0L
                        : studentJobTrackerRepository
                        .countUpcomingInterviews(
                                student.getId(),
                                LocalDateTime.now(),
                                activeInterviewStatuses
                        );

        List<OfferStatus> pendingOfferStatuses =
                resolveOfferStatuses(
                        "SENT",
                        "PENDING",
                        "VIEWED"
                );

        long pendingOffers =
                pendingOfferStatuses.isEmpty()
                        ? 0L
                        : studentJobTrackerRepository
                        .countPendingOffers(
                                student.getId(),
                                pendingOfferStatuses
                        );

        long interviewApplications =
                interviewScheduled + interviewCompleted;

        /*
         * If your application status changes past the interview
         * stage, include offered/hired applications as converted.
         */
        long interviewConversions =
                interviewApplications + offered + hired;

        long offerConversions = offered + hired;

        return JobTrackerDashboardResponse.builder()
                .totalApplications(totalApplications)
                .applied(applied)
                .underReview(underReview)
                .shortlisted(shortlisted)
                .interviewScheduled(interviewScheduled)
                .interviewCompleted(interviewCompleted)
                .offered(offered)
                .hired(hired)
                .rejected(rejected)
                .withdrawn(withdrawn)
                .upcomingInterviews(upcomingInterviews)
                .pendingOffers(pendingOffers)
                .interviewConversionRate(
                        percentage(
                                interviewConversions,
                                totalApplications
                        )
                )
                .offerConversionRate(
                        percentage(
                                offerConversions,
                                totalApplications
                        )
                )
                .selectionRate(
                        percentage(
                                hired,
                                totalApplications
                        )
                )
                .build();
    }

    @Override
    public Page<JobTrackerItemResponse> getApplications(
            ApplicationStatus status,
            String keyword,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    ) {

        Student student = getCurrentStudent();

        DateRange range =
                createDateRange(fromDate, toDate);

        String normalizedKeyword =
                normalizeKeyword(keyword);

        List<InterviewStatus> activeStatuses =
                resolveInterviewStatuses(
                        "SCHEDULED",
                        "RESCHEDULED",
                        "CONFIRMED"
                );

        /*
         * JPQL does not accept an empty collection safely in
         * every database provider. Add one valid fallback status.
         */
        if (activeStatuses.isEmpty()) {
            activeStatuses =
                    List.of(
                            InterviewStatus.values()[0]
                    );
        }

        return studentJobTrackerRepository
                .findApplicationTrackerItems(
                        student.getId(),
                        status,
                        normalizedKeyword,
                        range.from(),
                        range.toExclusive(),
                        activeStatuses,
                        pageable
                )
                .map(this::mapTrackerItem);
    }

    @Override
    public JobTrackerItemResponse getApplication(
            Long applicationId
    ) {

        Student student = getCurrentStudent();

        JobApplication application =
                getOwnedApplication(
                        applicationId,
                        student.getId()
                );

        List<Interview> interviews =
                studentJobTrackerRepository
                        .findApplicationInterviews(
                                applicationId,
                                student.getId()
                        );

        List<JobOffer> offers =
                studentJobTrackerRepository
                        .findApplicationOffers(
                                applicationId,
                                student.getId()
                        );

        Interview nextInterview =
                findNextInterview(interviews);

        JobOffer latestOffer =
                offers.stream()
                        .max(
                                Comparator.comparing(
                                        JobOffer::getCreatedAt,
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()
                                        )
                                )
                        )
                        .orElse(null);

        return mapApplication(
                application,
                nextInterview,
                latestOffer
        );
    }

    @Override
    public List<ApplicationStatusHistoryResponse>
    getApplicationHistory(
            Long applicationId
    ) {
        return applicationStatusHistoryService
                .getStudentApplicationHistory(applicationId);
    }

    @Override
    public List<ApplicationTimelineResponse>
    getApplicationTimeline(
            Long applicationId
    ) {

        Student student = getCurrentStudent();

        JobApplication application =
                getOwnedApplication(
                        applicationId,
                        student.getId()
                );

        List<Interview> interviews =
                studentJobTrackerRepository
                        .findApplicationInterviews(
                                applicationId,
                                student.getId()
                        );

        List<JobOffer> offers =
                studentJobTrackerRepository
                        .findApplicationOffers(
                                applicationId,
                                student.getId()
                        );

        List<ApplicationStatusHistory> histories =
                applicationStatusHistoryService
                        .getStudentApplicationHistoryEntities(
                                applicationId,
                                student.getId()
                        );

        List<ApplicationTimelineResponse> timeline =
                new ArrayList<>();

        if (histories.isEmpty()) {
            timeline.add(
                    buildSubmittedTimelineEvent(application)
            );
        } else {
            histories.stream()
                    .map(this::mapStatusHistoryTimeline)
                    .forEach(timeline::add);

            boolean hasSubmittedHistory = histories.stream()
                    .anyMatch(history ->
                            history.getNewStatus()
                                    == ApplicationStatus.SUBMITTED
                    );

            if (!hasSubmittedHistory) {
                timeline.add(
                        buildSubmittedTimelineEvent(application)
                );
            }
        }

        for (Interview interview : interviews) {
            timeline.add(
                    mapInterviewTimeline(interview)
            );
        }

        for (JobOffer offer : offers) {
            timeline.add(
                    mapOfferTimeline(offer)
            );
        }

        timeline.sort(
                Comparator.comparing(
                        ApplicationTimelineResponse::getOccurredAt,
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                )
        );

        return timeline;
    }

    @Override
    public List<UpcomingInterviewResponse>
    getUpcomingInterviews() {

        Student student = getCurrentStudent();

        List<InterviewStatus> activeStatuses =
                resolveInterviewStatuses(
                        "SCHEDULED",
                        "RESCHEDULED",
                        "CONFIRMED"
                );

        if (activeStatuses.isEmpty()) {
            return List.of();
        }

        return studentJobTrackerRepository
                .findUpcomingInterviews(
                        student.getId(),
                        LocalDateTime.now(),
                        activeStatuses
                )
                .stream()
                .map(this::mapUpcomingInterview)
                .toList();
    }

    @Override
    public List<OfferSummaryResponse> getOffers(
            OfferStatus status
    ) {

        Student student = getCurrentStudent();

        return studentJobTrackerRepository
                .findStudentOffers(
                        student.getId(),
                        status
                )
                .stream()
                .map(this::mapOfferSummary)
                .toList();
    }

    private JobTrackerItemResponse mapTrackerItem(
            StudentApplicationTrackerProjection projection
    ) {

        ApplicationStatus applicationStatus =
                parseEnum(
                        ApplicationStatus.class,
                        projection.getApplicationStatus()
                );

        OfferStatus offerStatus =
                parseEnum(
                        OfferStatus.class,
                        projection.getOfferStatus()
                );

        ActionDetails action =
                determineAction(
                        applicationStatus,
                        offerStatus,
                        projection.getNextInterviewAt()
                );

        return JobTrackerItemResponse.builder()
                .applicationId(
                        projection.getApplicationId()
                )
                .jobId(projection.getJobId())
                .jobTitle(projection.getJobTitle())
                .companyId(projection.getCompanyId())
                .companyName(projection.getCompanyName())
                .companyLogoUrl(
                        projection.getCompanyLogoUrl()
                )
                .location(projection.getLocation())
                .employmentType(
                        normalizeEnumText(
                                projection.getEmploymentType()
                        )
                )
                .workMode(
                        normalizeEnumText(
                                projection.getWorkMode()
                        )
                )
                .applicationStatus(applicationStatus)
                .appliedAt(projection.getAppliedAt())
                .lastUpdatedAt(
                        projection.getLastUpdatedAt()
                )
                .nextInterviewAt(
                        projection.getNextInterviewAt()
                )
                .nextInterviewType(
                        normalizeEnumText(
                                projection.getNextInterviewType()
                        )
                )
                .nextInterviewMode(
                        normalizeEnumText(
                                projection.getNextInterviewMode()
                        )
                )
                .offerId(projection.getOfferId())
                .offerStatus(offerStatus)
                .actionRequired(action.required())
                .actionMessage(action.message())
                .build();
    }

    private JobTrackerItemResponse mapApplication(
            JobApplication application,
            Interview nextInterview,
            JobOffer latestOffer
    ) {

        ApplicationStatus applicationStatus =
                application.getStatus();

        OfferStatus offerStatus =
                latestOffer == null
                        ? null
                        : latestOffer.getStatus();

        LocalDateTime nextInterviewAt =
                nextInterview == null
                        ? null
                        : nextInterview.getScheduledAt();

        ActionDetails action =
                determineAction(
                        applicationStatus,
                        offerStatus,
                        nextInterviewAt
                );

        return JobTrackerItemResponse.builder()
                .applicationId(application.getId())
                .jobId(
                        application
                                .getJobPosting()
                                .getId()
                )
                .jobTitle(
                        application
                                .getJobPosting()
                                .getTitle()
                )
                .companyId(
                        application
                                .getJobPosting()
                                .getCompany()
                                .getId()
                )
                .companyName(
                        application
                                .getJobPosting()
                                .getCompany()
                                .getName()
                )
                .companyLogoUrl(
                        application
                                .getJobPosting()
                                .getCompany()
                                .getLogoUrl()
                )
                .location(
                        application
                                .getJobPosting()
                                .getLocation()
                )
                .employmentType(
                        application
                                .getJobPosting()
                                .getEmploymentType()
                                .name()
                )
                .workMode(
                        application
                                .getJobPosting()
                                .getWorkMode()
                                .name()
                )
                .applicationStatus(applicationStatus)
                .appliedAt(application.getAppliedAt())
                .lastUpdatedAt(application.getUpdatedAt())
                .nextInterviewAt(nextInterviewAt)
                .nextInterviewType(
                        nextInterview == null
                                ? null
                                : nextInterview
                                .getInterviewType()
                                .name()
                )
                .nextInterviewMode(
                        nextInterview == null
                                ? null
                                : nextInterview
                                .getInterviewMode()
                                .name()
                )
                .offerId(
                        latestOffer == null
                                ? null
                                : latestOffer.getId()
                )
                .offerStatus(offerStatus)
                .actionRequired(action.required())
                .actionMessage(action.message())
                .build();
    }

    private UpcomingInterviewResponse mapUpcomingInterview(
            Interview interview
    ) {

        LocalDateTime scheduledAt =
                interview.getScheduledAt();

        LocalDate today = LocalDate.now();

        boolean isToday =
                scheduledAt != null
                        && scheduledAt.toLocalDate()
                        .isEqual(today);

        return UpcomingInterviewResponse.builder()
                .interviewId(interview.getId())
                .applicationId(
                        interview
                                .getJobApplication()
                                .getId()
                )
                .jobId(
                        interview
                                .getJobApplication()
                                .getJobPosting()
                                .getId()
                )
                .jobTitle(
                        interview
                                .getJobApplication()
                                .getJobPosting()
                                .getTitle()
                )
                .companyId(
                        interview
                                .getJobApplication()
                                .getJobPosting()
                                .getCompany()
                                .getId()
                )
                .companyName(
                        interview
                                .getJobApplication()
                                .getJobPosting()
                                .getCompany()
                                .getName()
                )
                .companyLogoUrl(
                        interview
                                .getJobApplication()
                                .getJobPosting()
                                .getCompany()
                                .getLogoUrl()
                )
                .interviewType(
                        interview.getInterviewType()
                )
                .interviewMode(
                        interview.getInterviewMode()
                )
                .interviewStatus(
                        interview.getStatus()
                )
                .scheduledAt(scheduledAt)
                .durationMinutes(
                        interview.getDurationMinutes()
                )
                .meetingLink(
                        interview.getMeetingUrl()
                )
                .location(interview.getLocation())
                .instructions(
                        interview.getInstructions()
                )
                .today(isToday)
                .actionRequired(true)
                .build();
    }

    private OfferSummaryResponse mapOfferSummary(
            JobOffer offer
    ) {

        LocalDate expiryDate = offer.getOfferExpiryDate();

        boolean expired =
                expiryDate != null
                        && expiryDate.isBefore(LocalDate.now());

        boolean actionable =
                isOfferActionable(
                        offer.getStatus(),
                        expired
                );

        String actionMessage = buildOfferActionMessage(
                offer.getStatus(),
                expired,
                actionable
        );

        return OfferSummaryResponse.builder()
                .offerId(offer.getId())
                .applicationId(
                        offer.getJobApplication().getId()
                )
                .jobId(
                        offer.getJobApplication()
                                .getJobPosting()
                                .getId()
                )
                .jobTitle(
                        offer.getJobApplication()
                                .getJobPosting()
                                .getTitle()
                )
                .companyId(
                        offer.getJobApplication()
                                .getJobPosting()
                                .getCompany()
                                .getId()
                )
                .companyName(
                        offer.getJobApplication()
                                .getJobPosting()
                                .getCompany()
                                .getName()
                )
                .companyLogoUrl(
                        offer.getJobApplication()
                                .getJobPosting()
                                .getCompany()
                                .getLogoUrl()
                )
                .offerTitle(offer.getOfferTitle())
                .annualCtc(offer.getAnnualCtc())
                .baseSalary(offer.getBaseSalary())
                .bonus(offer.getBonus())
                .currency(offer.getCurrency())
                .employmentType(offer.getEmploymentType())
                .workLocation(offer.getWorkLocation())
                .joiningDate(offer.getJoiningDate())
                .offerExpiryDate(expiryDate)
                .probationPeriodMonths(
                        offer.getProbationPeriodMonths()
                )
                .noticePeriodDays(
                        offer.getNoticePeriodDays()
                )
                .offerStatus(offer.getStatus())
                .sentAt(offer.getSentAt())
                .viewedAt(offer.getViewedAt())
                .respondedAt(
                        resolveOfferResponseTime(offer)
                )
                .offerLetterUrl(
                        offer.getOfferLetterUrl()
                )
                .expired(expired)
                .actionRequired(actionable)
                .actionMessage(actionMessage)
                .build();
    }

    private ApplicationTimelineResponse buildSubmittedTimelineEvent(
            JobApplication application
    ) {
        return ApplicationTimelineResponse.builder()
                .event("APPLICATION_SUBMITTED")
                .title("Application submitted")
                .description(
                        "You applied for "
                                + application.getJobPosting().getTitle()
                                + "."
                )
                .occurredAt(application.getAppliedAt())
                .completed(true)
                .sourceType("APPLICATION")
                .sourceId(application.getId())
                .build();
    }

    private ApplicationTimelineResponse mapStatusHistoryTimeline(
            ApplicationStatusHistory history
    ) {
        ApplicationStatus newStatus = history.getNewStatus();

        String normalizedStatus =
                newStatus == null
                        ? "UPDATED"
                        : normalizeStatus(newStatus.name());

        String title = switch (normalizedStatus) {
            case "SUBMITTED" -> "Application submitted";
            case "UNDER_REVIEW" -> "Application under review";
            case "SHORTLISTED" -> "Application shortlisted";
            case "ASSESSMENT_SCHEDULED" -> "Assessment scheduled";
            case "ASSESSMENT_COMPLETED" -> "Assessment completed";
            case "INTERVIEW_SCHEDULED" -> "Interview stage started";
            case "INTERVIEW_COMPLETED" -> "Interview stage completed";
            case "OFFERED" -> "Offer stage reached";
            case "HIRED" -> "Application selected";
            case "REJECTED" -> "Application rejected";
            case "WITHDRAWN" -> "Application withdrawn";
            default -> "Application status updated";
        };

        String description =
                history.getComment() == null
                        ? "Application status changed to "
                        + normalizedStatus
                        .replace('_', ' ')
                        .toLowerCase(Locale.ROOT)
                        + "."
                        : history.getComment();

        return ApplicationTimelineResponse.builder()
                .event("APPLICATION_" + normalizedStatus)
                .title(title)
                .description(description)
                .occurredAt(history.getCreatedAt())
                .completed(true)
                .sourceType("APPLICATION_STATUS_HISTORY")
                .sourceId(history.getId())
                .build();
    }

    private ApplicationTimelineResponse
    mapInterviewTimeline(
            Interview interview
    ) {

        String status =
                interview.getStatus() == null
                        ? "INTERVIEW"
                        : interview.getStatus().name();

        String title =
                switch (normalizeStatus(status)) {
                    case "SCHEDULED" ->
                            "Interview scheduled";

                    case "RESCHEDULED" ->
                            "Interview rescheduled";

                    case "COMPLETED" ->
                            "Interview completed";

                    case "CANCELLED" ->
                            "Interview cancelled";

                    default ->
                            "Interview updated";
                };

        String description =
                buildInterviewDescription(interview);

        LocalDateTime occurredAt =
                firstNonNull(
                        interview.getUpdatedAt(),
                        interview.getCreatedAt(),
                        interview.getScheduledAt()
                );

        return ApplicationTimelineResponse.builder()
                .event(
                        "INTERVIEW_" +
                                normalizeStatus(status)
                )
                .title(title)
                .description(description)
                .occurredAt(occurredAt)
                .completed(
                        isCompletedInterviewStatus(status)
                )
                .sourceType("INTERVIEW")
                .sourceId(interview.getId())
                .build();
    }

    private ApplicationTimelineResponse mapOfferTimeline(
            JobOffer offer
    ) {

        String normalizedStatus =
                offer.getStatus() == null
                        ? "CREATED"
                        : normalizeStatus(
                        offer.getStatus().name()
                );

        String title =
                switch (normalizedStatus) {
                    case "DRAFT" ->
                            "Offer prepared";
                    case "SENT" ->
                            "Offer received";
                    case "VIEWED" ->
                            "Offer viewed";
                    case "ACCEPTED" ->
                            "Offer accepted";
                    case "REJECTED" ->
                            "Offer rejected";
                    case "WITHDRAWN" ->
                            "Offer withdrawn";
                    case "EXPIRED" ->
                            "Offer expired";
                    default ->
                            "Offer updated";
                };

        String description =
                switch (normalizedStatus) {
                    case "DRAFT" ->
                            "The recruiter prepared a job offer.";
                    case "SENT" ->
                            "You received a job offer.";
                    case "VIEWED" ->
                            "You viewed the job offer.";
                    case "ACCEPTED" ->
                            "You accepted the job offer.";
                    case "REJECTED" ->
                            "You rejected the job offer.";
                    case "WITHDRAWN" ->
                            "The recruiter withdrew the offer.";
                    case "EXPIRED" ->
                            "The offer validity period ended.";
                    default ->
                            "A job offer update is available.";
                };

        LocalDateTime occurredAt =
                resolveOfferTimelineTime(
                        offer,
                        normalizedStatus
                );

        return ApplicationTimelineResponse.builder()
                .event("OFFER_" + normalizedStatus)
                .title(title)
                .description(description)
                .occurredAt(occurredAt)
                .completed(true)
                .sourceType("OFFER")
                .sourceId(offer.getId())
                .build();
    }

    private Interview findNextInterview(
            List<Interview> interviews
    ) {

        LocalDateTime now =
                LocalDateTime.now();

        Set<String> activeStatuses =
                Set.of(
                        "SCHEDULED",
                        "RESCHEDULED",
                        "CONFIRMED"
                );

        return interviews.stream()
                .filter(interview ->
                        interview.getScheduledAt() != null
                )
                .filter(interview ->
                        !interview.getScheduledAt()
                                .isBefore(now)
                )
                .filter(interview ->
                        interview.getStatus() != null
                                && activeStatuses.contains(
                                normalizeStatus(
                                        interview
                                                .getStatus()
                                                .name()
                                )
                        )
                )
                .min(
                        Comparator.comparing(
                                Interview::getScheduledAt
                        )
                )
                .orElse(null);
    }

    private ActionDetails determineAction(
            ApplicationStatus applicationStatus,
            OfferStatus offerStatus,
            LocalDateTime nextInterviewAt
    ) {

        if (offerStatus != null) {
            String normalizedOffer =
                    normalizeStatus(
                            offerStatus.name()
                    );

            if (Set.of(
                    "SENT",
                    "PENDING",
                    "VIEWED"
            ).contains(normalizedOffer)) {

                return new ActionDetails(
                        true,
                        "Review and respond to your job offer."
                );
            }
        }

        if (nextInterviewAt != null) {
            return new ActionDetails(
                    true,
                    "Prepare for your upcoming interview."
            );
        }

        if (applicationStatus != null
                && "WITHDRAWN".equals(
                normalizeStatus(
                        applicationStatus.name()
                )
        )) {

            return new ActionDetails(
                    false,
                    "Application withdrawn."
            );
        }

        return new ActionDetails(false, null);
    }

    private LocalDateTime resolveOfferResponseTime(
            JobOffer offer
    ) {

        return firstNonNull(
                offer.getAcceptedAt(),
                offer.getRejectedAt(),
                offer.getWithdrawnAt(),
                offer.getExpiredAt()
        );
    }

    private LocalDateTime resolveOfferTimelineTime(
            JobOffer offer,
            String normalizedStatus
    ) {

        return switch (normalizedStatus) {
            case "SENT" ->
                    firstNonNull(
                            offer.getSentAt(),
                            offer.getUpdatedAt(),
                            offer.getCreatedAt()
                    );

            case "VIEWED" ->
                    firstNonNull(
                            offer.getViewedAt(),
                            offer.getUpdatedAt(),
                            offer.getCreatedAt()
                    );

            case "ACCEPTED" ->
                    firstNonNull(
                            offer.getAcceptedAt(),
                            offer.getUpdatedAt(),
                            offer.getCreatedAt()
                    );

            case "REJECTED" ->
                    firstNonNull(
                            offer.getRejectedAt(),
                            offer.getUpdatedAt(),
                            offer.getCreatedAt()
                    );

            case "WITHDRAWN" ->
                    firstNonNull(
                            offer.getWithdrawnAt(),
                            offer.getUpdatedAt(),
                            offer.getCreatedAt()
                    );

            case "EXPIRED" ->
                    firstNonNull(
                            offer.getExpiredAt(),
                            offer.getUpdatedAt(),
                            offer.getCreatedAt()
                    );

            default ->
                    firstNonNull(
                            offer.getUpdatedAt(),
                            offer.getCreatedAt()
                    );
        };
    }

    private String buildOfferActionMessage(
            OfferStatus status,
            boolean expired,
            boolean actionable
    ) {

        if (actionable) {
            return "Review and respond to this offer.";
        }

        if (expired) {
            return "This offer has expired.";
        }

        if (status == null) {
            return null;
        }

        return switch (normalizeStatus(status.name())) {
            case "ACCEPTED" ->
                    "You accepted this offer.";
            case "REJECTED" ->
                    "You rejected this offer.";
            case "WITHDRAWN" ->
                    "The recruiter withdrew this offer.";
            case "EXPIRED" ->
                    "This offer has expired.";
            default ->
                    null;
        };
    }

    private boolean isOfferActionable(
            OfferStatus status,
            boolean expired
    ) {

        if (status == null || expired) {
            return false;
        }

        return Set.of(
                "SENT",
                "PENDING",
                "VIEWED"
        ).contains(
                normalizeStatus(status.name())
        );
    }

    private String buildInterviewDescription(
            Interview interview
    ) {

        String type =
                interview.getInterviewType() == null
                        ? "Interview"
                        : interview.getInterviewType()
                        .name()
                        .replace('_', ' ');

        String scheduledText =
                interview.getScheduledAt() == null
                        ? ""
                        : " scheduled for " +
                        interview.getScheduledAt();

        return type + scheduledText + ".";
    }

    private boolean isCompletedInterviewStatus(
            String status
    ) {

        return Set.of(
                "COMPLETED",
                "CANCELLED",
                "NO_SHOW"
        ).contains(normalizeStatus(status));
    }

    private JobApplication getOwnedApplication(
            Long applicationId,
            Long studentId
    ) {

        return studentJobTrackerRepository
                .findApplicationForStudent(
                        applicationId,
                        studentId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Application not found or does " +
                                        "not belong to the current student."
                        )
                );
    }

    private Student getCurrentStudent() {

        Long userId =
                securityUtils.getCurrentUserId();

        return studentRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student profile not found."
                        )
                );
    }

    private DateRange createDateRange(
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

        LocalDateTime toExclusive =
                toDate == null
                        ? null
                        : toDate.plusDays(1)
                        .atStartOfDay();

        return new DateRange(
                from,
                toExclusive
        );
    }

    private List<InterviewStatus>
    resolveInterviewStatuses(
            String... names
    ) {

        return Arrays.stream(names)
                .map(name ->
                        parseEnum(
                                InterviewStatus.class,
                                name
                        )
                )
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private List<OfferStatus> resolveOfferStatuses(
            String... names
    ) {

        return Arrays.stream(names)
                .map(name ->
                        parseEnum(
                                OfferStatus.class,
                                name
                        )
                )
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private <E extends Enum<E>> E parseEnum(
            Class<E> enumClass,
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Enum.valueOf(
                    enumClass,
                    normalizeStatus(value)
            );
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private long countAny(
            Map<String, Long> counts,
            String... statuses
    ) {

        long total = 0L;

        for (String status : statuses) {
            total += counts.getOrDefault(
                    normalizeStatus(status),
                    0L
            );
        }

        return total;
    }

    private double percentage(
            long numerator,
            long denominator
    ) {

        if (denominator <= 0) {
            return 0.0;
        }

        return BigDecimal.valueOf(numerator)
                .multiply(
                        BigDecimal.valueOf(100)
                )
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

    private String normalizeKeyword(
            String keyword
    ) {

        if (keyword == null
                || keyword.isBlank()) {
            return null;
        }

        return keyword.trim();
    }

    private String normalizeEnumText(
            String value
    ) {

        if (value == null) {
            return null;
        }

        int lastDot =
                value.lastIndexOf('.');

        return lastDot >= 0
                ? value.substring(lastDot + 1)
                : value;
    }

    private String normalizeStatus(
            String value
    ) {

        if (value == null) {
            return "";
        }

        String normalized =
                normalizeEnumText(value);

        return normalized
                .trim()
                .replace(' ', '_')
                .replace('-', '_')
                .toUpperCase(Locale.ROOT);
    }

    @SafeVarargs
    private final <T> T firstNonNull(
            T... values
    ) {

        for (T value : values) {
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    private record DateRange(
            LocalDateTime from,
            LocalDateTime toExclusive
    ) {
    }

    private record ActionDetails(
            boolean required,
            String message
    ) {
    }
}