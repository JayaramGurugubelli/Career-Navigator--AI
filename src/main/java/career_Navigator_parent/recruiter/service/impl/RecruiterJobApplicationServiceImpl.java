package career_Navigator_parent.recruiter.service.impl;

import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.company.entity.RecruiterProfile;
import career_Navigator_parent.company.repository.RecruiterProfileRepository;
import career_Navigator_parent.job.dto.request.UpdateJobApplicationStatusRequest;
import career_Navigator_parent.job.dto.request.UpdateRecruiterNotesRequest;
import career_Navigator_parent.job.dto.response.JobApplicationResponse;
import career_Navigator_parent.job.entity.JobApplication;
import career_Navigator_parent.job.entity.JobPosting;
import career_Navigator_parent.job.mapper.JobMapper;
import career_Navigator_parent.job.repository.JobApplicationRepository;
import career_Navigator_parent.job.repository.JobPostingRepository;
import career_Navigator_parent.job.service.ApplicationStatusHistoryService;
import career_Navigator_parent.recruiter.service.RecruiterJobApplicationService;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.shared.enums.ApplicationStatus;
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

    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final ApplicationStatusHistoryService
            applicationStatusHistoryService;
    private final JobMapper jobMapper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getApplicationsForJob(
            Long jobId,
            ApplicationStatus status,
            Pageable pageable
    ) {
        RecruiterProfile recruiter = getCurrentRecruiter();

        JobPosting jobPosting = getRecruiterJob(
                jobId,
                recruiter.getId()
        );

        Page<JobApplication> applications =
                status == null
                        ? jobApplicationRepository.findByJobPostingId(
                                jobPosting.getId(),
                                pageable
                        )
                        : jobApplicationRepository
                        .findByJobPostingIdAndStatus(
                                jobPosting.getId(),
                                status,
                                pageable
                        );

        return applications.map(jobMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public JobApplicationResponse getApplicationById(
            Long applicationId
    ) {
        RecruiterProfile recruiter = getCurrentRecruiter();

        return jobMapper.toResponse(
                getRecruiterApplication(
                        applicationId,
                        recruiter.getId()
                )
        );
    }

    @Override
    public JobApplicationResponse updateApplicationStatus(
            Long applicationId,
            UpdateJobApplicationStatusRequest request
    ) {
        RecruiterProfile recruiter = getCurrentRecruiter();

        JobApplication application = getRecruiterApplication(
                applicationId,
                recruiter.getId()
        );

        ApplicationStatus previousStatus = application.getStatus();
        ApplicationStatus newStatus = request.getStatus();

        validateStatusTransition(previousStatus, newStatus);

        LocalDateTime changedAt = LocalDateTime.now();
        application.setStatus(newStatus);
        application.setLastStatusChangedAt(changedAt);

        JobApplication updatedApplication =
                jobApplicationRepository.save(application);

        applicationStatusHistoryService.recordStatusChange(
                updatedApplication,
                previousStatus,
                newStatus,
                recruiter.getUser(),
                request.getComment()
        );

        return jobMapper.toResponse(updatedApplication);
    }

    @Override
    public JobApplicationResponse updateRecruiterNotes(
            Long applicationId,
            UpdateRecruiterNotesRequest request
    ) {
        RecruiterProfile recruiter = getCurrentRecruiter();

        JobApplication application = getRecruiterApplication(
                applicationId,
                recruiter.getId()
        );

        application.setRecruiterNotes(
                normalizeText(request.getNotes())
        );

        return jobMapper.toResponse(
                jobApplicationRepository.save(application)
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

    private JobPosting getRecruiterJob(
            Long jobId,
            Long recruiterId
    ) {
        return jobPostingRepository
                .findByIdAndRecruiterId(jobId, recruiterId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job posting not found or does not belong "
                                        + "to the current recruiter."
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
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job application not found or does not "
                                        + "belong to the current recruiter."
                        )
                );
    }

    private void validateStatusTransition(
            ApplicationStatus currentStatus,
            ApplicationStatus newStatus
    ) {
        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "New application status is required."
            );
        }

        if (currentStatus == newStatus) {
            throw new IllegalStateException(
                    "Application is already in "
                            + currentStatus
                            + " status."
            );
        }

        if (currentStatus == ApplicationStatus.WITHDRAWN
                || currentStatus == ApplicationStatus.REJECTED
                || currentStatus == ApplicationStatus.HIRED) {
            throw new IllegalStateException(
                    "Application status cannot be changed from "
                            + currentStatus
                            + "."
            );
        }

        boolean validTransition = switch (currentStatus) {
            case SUBMITTED ->
                    newStatus == ApplicationStatus.UNDER_REVIEW
                            || newStatus == ApplicationStatus.REJECTED;

            case UNDER_REVIEW ->
                    newStatus == ApplicationStatus.SHORTLISTED
                            || newStatus == ApplicationStatus.REJECTED;

            case SHORTLISTED ->
                    newStatus == ApplicationStatus.ASSESSMENT_SCHEDULED
                            || newStatus == ApplicationStatus.INTERVIEW_SCHEDULED
                            || newStatus == ApplicationStatus.REJECTED;

            case ASSESSMENT_SCHEDULED ->
                    newStatus == ApplicationStatus.ASSESSMENT_COMPLETED
                            || newStatus == ApplicationStatus.REJECTED;

            case ASSESSMENT_COMPLETED ->
                    newStatus == ApplicationStatus.INTERVIEW_SCHEDULED
                            || newStatus == ApplicationStatus.REJECTED;

            case INTERVIEW_SCHEDULED ->
                    newStatus == ApplicationStatus.INTERVIEW_COMPLETED
                            || newStatus == ApplicationStatus.REJECTED;

            case INTERVIEW_COMPLETED ->
                    newStatus == ApplicationStatus.OFFERED
                            || newStatus == ApplicationStatus.REJECTED;

            case OFFERED ->
                    newStatus == ApplicationStatus.HIRED
                            || newStatus == ApplicationStatus.REJECTED;

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

    private String normalizeText(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}
