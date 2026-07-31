package career_Navigator_parent.interview.service.impl;

import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.company.entity.RecruiterProfile;
import career_Navigator_parent.company.repository.RecruiterProfileRepository;
import career_Navigator_parent.interview.dto.request.CreateInterviewRequest;
import career_Navigator_parent.interview.dto.request.UpdateInterviewFeedbackRequest;
import career_Navigator_parent.interview.dto.request.UpdateInterviewRequest;
import career_Navigator_parent.interview.dto.request.UpdateInterviewStatusRequest;
import career_Navigator_parent.interview.dto.response.InterviewResponse;
import career_Navigator_parent.interview.entity.Interview;
import career_Navigator_parent.interview.enums.InterviewMode;
import career_Navigator_parent.interview.enums.InterviewResult;
import career_Navigator_parent.interview.enums.InterviewStatus;
import career_Navigator_parent.interview.mapper.InterviewMapper;
import career_Navigator_parent.interview.repository.InterviewRepository;
import career_Navigator_parent.interview.service.RecruiterInterviewService;
import career_Navigator_parent.job.entity.ApplicationStatusHistory;
import career_Navigator_parent.job.entity.JobApplication;
import career_Navigator_parent.job.repository.ApplicationStatusHistoryRepository;
import career_Navigator_parent.job.repository.JobApplicationRepository;
import career_Navigator_parent.notification.enums.NotificationReferenceType;
import career_Navigator_parent.notification.enums.NotificationType;
import career_Navigator_parent.notification.service.NotificationService;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.shared.enums.ApplicationStatus;
import career_Navigator_parent.user.entity.User;
import career_Navigator_parent.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RecruiterInterviewServiceImpl
        implements RecruiterInterviewService {

    private final InterviewRepository interviewRepository;

    private final JobApplicationRepository
            jobApplicationRepository;

    private final RecruiterProfileRepository
            recruiterProfileRepository;

    private final ApplicationStatusHistoryRepository
            applicationStatusHistoryRepository;

    private final UserRepository userRepository;

    private final InterviewMapper interviewMapper;

    private final NotificationService notificationService;

    private final SecurityUtils securityUtils;

    private static final Set<ApplicationStatus>
            INTERVIEW_ALLOWED_APPLICATION_STATUSES =
            EnumSet.of(
                    ApplicationStatus.SHORTLISTED,
                    ApplicationStatus.ASSESSMENT_COMPLETED
            );

    private static final Set<InterviewStatus>
            ACTIVE_INTERVIEW_STATUSES =
            EnumSet.of(
                    InterviewStatus.SCHEDULED,
                    InterviewStatus.CONFIRMED,
                    InterviewStatus.RESCHEDULED
            );

    @Override
    public InterviewResponse createInterview(
            Long applicationId,
            CreateInterviewRequest request
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        User currentUser =
                getCurrentUser();

        JobApplication application =
                getRecruiterApplication(
                        applicationId,
                        recruiter.getId()
                );

        validateApplicationForInterview(application);

        validateInterviewSchedule(
                request.getScheduledAt(),
                request.getEndAt(),
                request.getDurationMinutes()
        );

        validateInterviewMode(
                request.getInterviewMode(),
                request.getMeetingUrl(),
                request.getLocation()
        );

        boolean activeInterviewExists =
                interviewRepository
                        .existsByJobApplicationIdAndStatusIn(
                                application.getId(),
                                ACTIVE_INTERVIEW_STATUSES
                        );

        if (activeInterviewExists) {
            throw new IllegalStateException(
                    "An active interview already exists for this application."
            );
        }

        Interview interview =
                interviewMapper.toEntity(request);

        interview.setJobApplication(application);
        interview.setRecruiter(recruiter);
        interview.setStatus(
                InterviewStatus.SCHEDULED
        );
        interview.setResult(
                InterviewResult.PENDING
        );

        Interview savedInterview =
                interviewRepository.save(interview);

        ApplicationStatus oldStatus =
                application.getStatus();

        application.setStatus(
                ApplicationStatus.INTERVIEW_SCHEDULED
        );

        application.setLastStatusChangedAt(
                LocalDateTime.now()
        );

        jobApplicationRepository.save(application);

        saveApplicationStatusHistory(
                application,
                oldStatus,
                ApplicationStatus.INTERVIEW_SCHEDULED,
                currentUser,
                "Interview scheduled: "
                        + savedInterview.getTitle()
        );

        notificationService.createNotification(
                application.getStudent().getUser(),
                NotificationType.INTERVIEW_SCHEDULED,
                "Interview scheduled",
                "Your "
                        + savedInterview.getInterviewType()
                        + " interview for "
                        + application.getJobPosting().getTitle()
                        + " is scheduled for "
                        + savedInterview.getScheduledAt()
                        + ".",
                NotificationReferenceType.INTERVIEW,
                savedInterview.getId(),
                "/student/interviews/"
                        + savedInterview.getId()
        );

        return interviewMapper.toResponse(
                savedInterview
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InterviewResponse> getMyInterviews(
            InterviewStatus status,
            Pageable pageable
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        Page<Interview> interviews;

        if (status == null) {

            interviews =
                    interviewRepository
                            .findByRecruiterId(
                                    recruiter.getId(),
                                    pageable
                            );

        } else {

            interviews =
                    interviewRepository
                            .findByRecruiterIdAndStatus(
                                    recruiter.getId(),
                                    status,
                                    pageable
                            );
        }

        return interviews.map(
                interviewMapper::toResponse
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InterviewResponse> getApplicationInterviews(
            Long applicationId,
            Pageable pageable
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        getRecruiterApplication(
                applicationId,
                recruiter.getId()
        );

        return interviewRepository
                .findByJobApplicationIdAndRecruiterId(
                        applicationId,
                        recruiter.getId(),
                        pageable
                )
                .map(interviewMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewResponse getInterviewById(
            Long interviewId
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        Interview interview =
                getRecruiterInterview(
                        interviewId,
                        recruiter.getId()
                );

        return interviewMapper.toResponse(interview);
    }

    @Override
    public InterviewResponse updateInterview(
            Long interviewId,
            UpdateInterviewRequest request
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        Interview interview =
                getRecruiterInterview(
                        interviewId,
                        recruiter.getId()
                );

        validateInterviewCanBeEdited(interview);

        validateInterviewSchedule(
                request.getScheduledAt(),
                request.getEndAt(),
                request.getDurationMinutes()
        );

        validateInterviewMode(
                request.getInterviewMode(),
                request.getMeetingUrl(),
                request.getLocation()
        );

        boolean scheduleChanged =
                !interview.getScheduledAt()
                        .equals(request.getScheduledAt())
                        ||
                        !interview.getEndAt()
                                .equals(request.getEndAt());

        interviewMapper.updateEntity(
                interview,
                request
        );

        if (scheduleChanged) {

            interview.setStatus(
                    InterviewStatus.RESCHEDULED
            );

            interview.setConfirmedAt(null);
            interview.setDeclinedAt(null);
            interview.setStudentResponseNotes(null);
        }

        Interview updatedInterview =
                interviewRepository.save(interview);

        if (scheduleChanged) {

            notificationService.createNotification(
                    updatedInterview
                            .getJobApplication()
                            .getStudent()
                            .getUser(),
                    NotificationType.INTERVIEW_RESCHEDULED,
                    "Interview rescheduled",
                    "Your interview for "
                            + updatedInterview
                            .getJobApplication()
                            .getJobPosting()
                            .getTitle()
                            + " has been rescheduled to "
                            + updatedInterview.getScheduledAt()
                            + ".",
                    NotificationReferenceType.INTERVIEW,
                    updatedInterview.getId(),
                    "/student/interviews/"
                            + updatedInterview.getId()
            );
        }

        return interviewMapper.toResponse(
                updatedInterview
        );
    }

    @Override
    public InterviewResponse updateInterviewStatus(
            Long interviewId,
            UpdateInterviewStatusRequest request
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        Interview interview =
                getRecruiterInterview(
                        interviewId,
                        recruiter.getId()
                );

        InterviewStatus currentStatus =
                interview.getStatus();

        InterviewStatus newStatus =
                request.getStatus();

        validateRecruiterStatusTransition(
                currentStatus,
                newStatus
        );

        LocalDateTime now =
                LocalDateTime.now();

        interview.setStatus(newStatus);

        if (newStatus ==
                InterviewStatus.CANCELLED) {

            interview.setCancelledAt(now);
        }

        if (newStatus ==
                InterviewStatus.MISSED) {

            validateInterviewCanBeMarkedMissed(
                    interview,
                    now
            );
        }

        if (request.getComment() != null &&
                !request.getComment().isBlank()) {

            interview.setInstructions(
                    appendComment(
                            interview.getInstructions(),
                            request.getComment()
                    )
            );
        }

        Interview updatedInterview =
                interviewRepository.save(interview);

        if (newStatus == InterviewStatus.CANCELLED) {

            notificationService.createNotification(
                    updatedInterview
                            .getJobApplication()
                            .getStudent()
                            .getUser(),
                    NotificationType.INTERVIEW_CANCELLED,
                    "Interview cancelled",
                    "Your interview for "
                            + updatedInterview
                            .getJobApplication()
                            .getJobPosting()
                            .getTitle()
                            + " has been cancelled.",
                    NotificationReferenceType.INTERVIEW,
                    updatedInterview.getId(),
                    "/student/interviews/"
                            + updatedInterview.getId()
            );
        }

        return interviewMapper.toResponse(
                updatedInterview
        );
    }

    @Override
    public InterviewResponse updateInterviewFeedback(
            Long interviewId,
            UpdateInterviewFeedbackRequest request
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        User currentUser =
                getCurrentUser();

        Interview interview =
                getRecruiterInterview(
                        interviewId,
                        recruiter.getId()
                );

        validateFeedbackSubmission(
                interview,
                request
        );

        LocalDateTime now =
                LocalDateTime.now();

        interview.setFeedback(
                normalizeText(request.getFeedback())
        );

        interview.setStrengths(
                normalizeText(request.getStrengths())
        );

        interview.setAreasForImprovement(
                normalizeText(
                        request.getAreasForImprovement()
                )
        );

        interview.setTechnicalScore(
                request.getTechnicalScore()
        );

        interview.setCommunicationScore(
                request.getCommunicationScore()
        );

        interview.setProblemSolvingScore(
                request.getProblemSolvingScore()
        );

        interview.setOverallScore(
                request.getOverallScore()
        );

        interview.setResult(
                request.getResult()
        );

        interview.setStatus(
                InterviewStatus.COMPLETED
        );

        interview.setCompletedAt(now);

        Interview completedInterview =
                interviewRepository.save(interview);

        JobApplication application =
                interview.getJobApplication();

        if (application.getStatus() !=
                ApplicationStatus.INTERVIEW_COMPLETED) {

            ApplicationStatus oldStatus =
                    application.getStatus();

            if (oldStatus !=
                    ApplicationStatus.INTERVIEW_SCHEDULED) {

                throw new IllegalStateException(
                        "Application must be in INTERVIEW_SCHEDULED status before completing the interview."
                );
            }

            application.setStatus(
                    ApplicationStatus.INTERVIEW_COMPLETED
            );

            application.setLastStatusChangedAt(now);

            jobApplicationRepository.save(application);

            saveApplicationStatusHistory(
                    application,
                    oldStatus,
                    ApplicationStatus.INTERVIEW_COMPLETED,
                    currentUser,
                    "Interview completed. Result: "
                            + request.getResult()
            );
        }

        notificationService.createNotification(
                application.getStudent().getUser(),
                NotificationType.INTERVIEW_COMPLETED,
                "Interview completed",
                "Your interview for "
                        + application.getJobPosting().getTitle()
                        + " has been completed.",
                NotificationReferenceType.INTERVIEW,
                completedInterview.getId(),
                "/student/interviews/"
                        + completedInterview.getId()
        );

        return interviewMapper.toResponse(
                completedInterview
        );
    }

    @Override
    public void deleteInterview(
            Long interviewId
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        Interview interview =
                getRecruiterInterview(
                        interviewId,
                        recruiter.getId()
                );

        if (interview.getStatus() ==
                InterviewStatus.COMPLETED) {

            throw new IllegalStateException(
                    "Completed interview cannot be deleted."
            );
        }

        if (interview.getStatus() ==
                InterviewStatus.CONFIRMED) {

            throw new IllegalStateException(
                    "Confirmed interview cannot be deleted. Cancel it instead."
            );
        }

        if (interview.getStatus() ==
                InterviewStatus.RESCHEDULED) {

            throw new IllegalStateException(
                    "Rescheduled interview cannot be deleted. Cancel it instead."
            );
        }

        interviewRepository.delete(interview);
    }

    private RecruiterProfile getCurrentRecruiter() {

        Long userId =
                securityUtils.getCurrentUserId();

        return recruiterProfileRepository
                .findByUserIdAndActiveTrue(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Active recruiter profile not found."
                        )
                );
    }

    private User getCurrentUser() {

        Long userId =
                securityUtils.getCurrentUserId();

        return userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found."
                        )
                );
    }

    private JobApplication getRecruiterApplication(
            Long applicationId,
            Long recruiterId
    ) {

        return jobApplicationRepository
                .findByIdAndJobPostingRecruiterId(
                        applicationId,
                        recruiterId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Job application not found or does not belong to the current recruiter."
                        )
                );
    }

    private Interview getRecruiterInterview(
            Long interviewId,
            Long recruiterId
    ) {

        return interviewRepository
                .findByIdAndRecruiterId(
                        interviewId,
                        recruiterId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Interview not found or does not belong to the current recruiter."
                        )
                );
    }

    private void validateApplicationForInterview(
            JobApplication application
    ) {

        ApplicationStatus status =
                application.getStatus();

        if (status ==
                ApplicationStatus.WITHDRAWN) {

            throw new IllegalStateException(
                    "Interview cannot be scheduled for a withdrawn application."
            );
        }

        if (status ==
                ApplicationStatus.REJECTED) {

            throw new IllegalStateException(
                    "Interview cannot be scheduled for a rejected application."
            );
        }

        if (status ==
                ApplicationStatus.HIRED) {

            throw new IllegalStateException(
                    "Interview cannot be scheduled for a hired application."
            );
        }

        if (status ==
                ApplicationStatus.INTERVIEW_SCHEDULED) {

            throw new IllegalStateException(
                    "An interview has already been scheduled for this application."
            );
        }

        if (status ==
                ApplicationStatus.INTERVIEW_COMPLETED) {

            throw new IllegalStateException(
                    "Interview process has already been completed for this application."
            );
        }

        if (!INTERVIEW_ALLOWED_APPLICATION_STATUSES
                .contains(status)) {

            throw new IllegalStateException(
                    "Interview can only be scheduled for SHORTLISTED or ASSESSMENT_COMPLETED applications."
            );
        }
    }

    private void validateInterviewSchedule(
            LocalDateTime scheduledAt,
            LocalDateTime endAt,
            Integer durationMinutes
    ) {

        LocalDateTime now =
                LocalDateTime.now();

        if (scheduledAt == null ||
                endAt == null) {

            throw new IllegalArgumentException(
                    "Interview start and end times are required."
            );
        }

        if (!scheduledAt.isAfter(now)) {

            throw new IllegalStateException(
                    "Interview must be scheduled in the future."
            );
        }

        if (!endAt.isAfter(scheduledAt)) {

            throw new IllegalStateException(
                    "Interview end time must be after the scheduled start time."
            );
        }

        long calculatedDuration =
                Duration.between(
                        scheduledAt,
                        endAt
                ).toMinutes();

        if (calculatedDuration !=
                durationMinutes.longValue()) {

            throw new IllegalStateException(
                    "Duration does not match the difference between scheduledAt and endAt."
            );
        }

        if (calculatedDuration < 10 ||
                calculatedDuration > 480) {

            throw new IllegalStateException(
                    "Interview duration must be between 10 and 480 minutes."
            );
        }
    }

    private void validateInterviewMode(
            InterviewMode interviewMode,
            String meetingUrl,
            String location
    ) {

        if (interviewMode ==
                InterviewMode.ONLINE) {

            if (meetingUrl == null ||
                    meetingUrl.isBlank()) {

                throw new IllegalStateException(
                        "Meeting URL is required for an online interview."
                );
            }
        }

        if (interviewMode ==
                InterviewMode.IN_PERSON) {

            if (location == null ||
                    location.isBlank()) {

                throw new IllegalStateException(
                        "Location is required for an in-person interview."
                );
            }
        }
    }

    private void validateInterviewCanBeEdited(
            Interview interview
    ) {

        if (interview.getStatus() ==
                InterviewStatus.COMPLETED) {

            throw new IllegalStateException(
                    "Completed interview cannot be edited."
            );
        }

        if (interview.getStatus() ==
                InterviewStatus.CANCELLED) {

            throw new IllegalStateException(
                    "Cancelled interview cannot be edited."
            );
        }

        if (interview.getStatus() ==
                InterviewStatus.MISSED) {

            throw new IllegalStateException(
                    "Missed interview cannot be edited."
            );
        }

        if (!LocalDateTime.now().isBefore(
                interview.getEndAt()
        )) {

            throw new IllegalStateException(
                    "Interview cannot be edited after its end time."
            );
        }
    }

    private void validateRecruiterStatusTransition(
            InterviewStatus currentStatus,
            InterviewStatus newStatus
    ) {

        if (currentStatus == newStatus) {

            throw new IllegalStateException(
                    "Interview is already in "
                            + currentStatus
                            + " status."
            );
        }

        if (currentStatus ==
                InterviewStatus.COMPLETED ||
                currentStatus ==
                        InterviewStatus.CANCELLED ||
                currentStatus ==
                        InterviewStatus.MISSED) {

            throw new IllegalStateException(
                    "Interview status cannot be changed from "
                            + currentStatus
                            + "."
            );
        }

        boolean validTransition =
                switch (currentStatus) {

                    case SCHEDULED ->
                            newStatus ==
                                    InterviewStatus.CANCELLED ||
                                    newStatus ==
                                            InterviewStatus.MISSED;

                    case CONFIRMED ->
                            newStatus ==
                                    InterviewStatus.CANCELLED ||
                                    newStatus ==
                                            InterviewStatus.MISSED;

                    case DECLINED ->
                            newStatus ==
                                    InterviewStatus.CANCELLED;

                    case RESCHEDULED ->
                            newStatus ==
                                    InterviewStatus.CANCELLED ||
                                    newStatus ==
                                            InterviewStatus.MISSED;

                    default -> false;
                };

        if (!validTransition) {

            throw new IllegalStateException(
                    "Invalid interview status transition from "
                            + currentStatus
                            + " to "
                            + newStatus
                            + "."
            );
        }
    }

    private void validateInterviewCanBeMarkedMissed(
            Interview interview,
            LocalDateTime now
    ) {

        if (now.isBefore(interview.getEndAt())) {

            throw new IllegalStateException(
                    "Interview cannot be marked MISSED before its end time."
            );
        }
    }

    private void validateFeedbackSubmission(
            Interview interview,
            UpdateInterviewFeedbackRequest request
    ) {

        if (interview.getStatus() ==
                InterviewStatus.COMPLETED) {

            throw new IllegalStateException(
                    "Interview feedback has already been submitted."
            );
        }

        if (interview.getStatus() ==
                InterviewStatus.CANCELLED) {

            throw new IllegalStateException(
                    "Feedback cannot be submitted for a cancelled interview."
            );
        }

        if (interview.getStatus() ==
                InterviewStatus.DECLINED) {

            throw new IllegalStateException(
                    "Feedback cannot be submitted for a declined interview."
            );
        }

        if (interview.getStatus() ==
                InterviewStatus.MISSED) {

            throw new IllegalStateException(
                    "Feedback cannot be submitted for a missed interview."
            );
        }

        if (LocalDateTime.now().isBefore(
                interview.getScheduledAt()
        )) {

            throw new IllegalStateException(
                    "Interview feedback cannot be submitted before the scheduled start time."
            );
        }

        if (request.getResult() ==
                InterviewResult.PENDING) {

            throw new IllegalStateException(
                    "Completed interview result cannot remain PENDING."
            );
        }
    }

    private void saveApplicationStatusHistory(
            JobApplication application,
            ApplicationStatus previousStatus,
            ApplicationStatus newStatus,
            User changedBy,
            String comment
    ) {

        ApplicationStatusHistory history =
                ApplicationStatusHistory.builder()
                        .application(application)
                        .previousStatus(previousStatus)
                        .newStatus(newStatus)
                        .changedBy(changedBy)
                        .comment(normalizeText(comment))
                        .build();

        applicationStatusHistoryRepository.save(
                history
        );
    }

    private String appendComment(
            String existingText,
            String comment
    ) {

        String normalizedComment =
                normalizeText(comment);

        if (normalizedComment == null) {
            return existingText;
        }

        if (existingText == null ||
                existingText.isBlank()) {

            return normalizedComment;
        }

        return existingText.trim()
                + System.lineSeparator()
                + normalizedComment;
    }

    private String normalizeText(
            String value
    ) {

        if (value == null ||
                value.isBlank()) {

            return null;
        }

        return value.trim();
    }
}