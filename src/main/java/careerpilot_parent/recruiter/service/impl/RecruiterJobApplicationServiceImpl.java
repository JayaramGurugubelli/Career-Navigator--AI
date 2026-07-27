package careerpilot_parent.recruiter.service.impl;

import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.company.enums.ApplicationStatus;
import careerpilot_parent.job.dto.request.UpdateJobApplicationStatusRequest;
import careerpilot_parent.job.dto.request.UpdateRecruiterNotesRequest;
import careerpilot_parent.job.dto.response.JobApplicationResponse;
import careerpilot_parent.job.entity.ApplicationStatusHistory;
import careerpilot_parent.job.entity.JobApplication;
import careerpilot_parent.job.entity.JobPosting;
import careerpilot_parent.job.mapper.JobMapper;
import careerpilot_parent.job.repository.ApplicationStatusHistoryRepository;
import careerpilot_parent.job.repository.JobApplicationRepository;
import careerpilot_parent.job.repository.JobPostingRepository;
import careerpilot_parent.company.entity.RecruiterProfile;
import careerpilot_parent.company.repository.RecruiterProfileRepository;
import careerpilot_parent.recruiter.service.RecruiterJobApplicationService;
import careerpilot_parent.security.util.SecurityUtils;
import careerpilot_parent.user.entity.User;
import careerpilot_parent.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RecruiterJobApplicationServiceImpl implements RecruiterJobApplicationService {

    private final JobApplicationRepository
            jobApplicationRepository;

    private final JobPostingRepository
            jobPostingRepository;

    private final ApplicationStatusHistoryRepository
            applicationStatusHistoryRepository;

    private final RecruiterProfileRepository
            recruiterProfileRepository;

    private final UserRepository userRepository;

    private final JobMapper jobMapper;

    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getApplicationsForJob(
            Long jobId,
            ApplicationStatus status,
            Pageable pageable
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        JobPosting jobPosting =
                getRecruiterJob(
                        jobId,
                        recruiter.getId()
                );

        Page<JobApplication> applications;

        if (status == null) {
            applications =
                    jobApplicationRepository
                            .findByJobPostingId(
                                    jobPosting.getId(),
                                    pageable
                            );
        } else {
            applications =
                    jobApplicationRepository
                            .findByJobPostingIdAndStatus(
                                    jobPosting.getId(),
                                    status,
                                    pageable
                            );
        }

        return applications.map(
                jobMapper::toResponse
        );
    }

    @Override
    @Transactional(readOnly = true)
    public JobApplicationResponse getApplicationById(
            Long applicationId
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        JobApplication application =
                getRecruiterApplication(
                        applicationId,
                        recruiter.getId()
                );

        return jobMapper.toResponse(application);
    }

    @Override
    public JobApplicationResponse updateApplicationStatus(
            Long applicationId,
            UpdateJobApplicationStatusRequest request
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

        ApplicationStatus oldStatus =
                application.getStatus();

        ApplicationStatus newStatus =
                request.getStatus();

        validateStatusTransition(
                oldStatus,
                newStatus
        );

        application.setStatus(newStatus);
        application.setLastStatusChangedAt(
                LocalDateTime.now()
        );

        JobApplication updatedApplication =
                jobApplicationRepository.save(
                        application
                );

        ApplicationStatusHistory history =
                ApplicationStatusHistory.builder()
                        .application(updatedApplication)
                        .previousStatus(oldStatus)
                        .newStatus(newStatus)
                        .changedBy(currentUser)
                        .comment(
                                normalizeText(
                                        request.getComment()
                                )
                        )
                        .build();

        applicationStatusHistoryRepository.save(history);

        return jobMapper.toResponse(
                updatedApplication
        );
    }

    @Override
    public JobApplicationResponse updateRecruiterNotes(
            Long applicationId,
            UpdateRecruiterNotesRequest request
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        JobApplication application =
                getRecruiterApplication(
                        applicationId,
                        recruiter.getId()
                );

        application.setRecruiterNotes(
                normalizeText(request.getNotes())
        );

        JobApplication updatedApplication =
                jobApplicationRepository.save(
                        application
                );

        return jobMapper.toResponse(
                updatedApplication
        );
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

    private JobPosting getRecruiterJob(
            Long jobId,
            Long recruiterId
    ) {

        return jobPostingRepository
                .findByIdAndRecruiterId(
                        jobId,
                        recruiterId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Job posting not found or does not belong to the current recruiter."
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

    private void validateStatusTransition(
            ApplicationStatus currentStatus,
            ApplicationStatus newStatus
    ) {

        if (currentStatus == newStatus) {
            throw new IllegalStateException(
                    "Application is already in "
                            + currentStatus
                            + " status."
            );
        }

        if (currentStatus == ApplicationStatus.WITHDRAWN ||
                currentStatus == ApplicationStatus.REJECTED ||
                currentStatus == ApplicationStatus.HIRED) {

            throw new IllegalStateException(
                    "Application status cannot be changed from "
                            + currentStatus
                            + "."
            );
        }

        boolean validTransition =
                switch (currentStatus) {

                    case SUBMITTED ->
                            newStatus ==
                                    ApplicationStatus.UNDER_REVIEW ||
                                    newStatus ==
                                            ApplicationStatus.REJECTED;

                    case UNDER_REVIEW ->
                            newStatus ==
                                    ApplicationStatus.SHORTLISTED ||
                                    newStatus ==
                                            ApplicationStatus.REJECTED;

                    case SHORTLISTED ->
                            newStatus ==
                                    ApplicationStatus.ASSESSMENT_SCHEDULED ||
                                    newStatus ==
                                            ApplicationStatus.INTERVIEW_SCHEDULED ||
                                    newStatus ==
                                            ApplicationStatus.REJECTED;

                    case ASSESSMENT_SCHEDULED ->
                            newStatus ==
                                    ApplicationStatus.ASSESSMENT_COMPLETED ||
                                    newStatus ==
                                            ApplicationStatus.REJECTED;

                    case ASSESSMENT_COMPLETED ->
                            newStatus ==
                                    ApplicationStatus.INTERVIEW_SCHEDULED ||
                                    newStatus ==
                                            ApplicationStatus.REJECTED;

                    case INTERVIEW_SCHEDULED ->
                            newStatus ==
                                    ApplicationStatus.INTERVIEW_COMPLETED ||
                                    newStatus ==
                                            ApplicationStatus.REJECTED;

                    case INTERVIEW_COMPLETED ->
                            newStatus ==
                                    ApplicationStatus.OFFERED ||
                                    newStatus ==
                                            ApplicationStatus.REJECTED;

                    case OFFERED ->
                            newStatus ==
                                    ApplicationStatus.HIRED ||
                                    newStatus ==
                                            ApplicationStatus.REJECTED;

                    default -> false;
                };

        if (!validTransition) {
            throw new IllegalStateException(
                    "Invalid application status transition from "
                            + currentStatus
                            + " to "
                            + newStatus
                            + "."
            );
        }
    }

    private String normalizeText(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}