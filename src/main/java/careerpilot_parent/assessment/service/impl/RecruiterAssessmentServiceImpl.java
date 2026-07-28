package careerpilot_parent.assessment.service.impl;

import careerpilot_parent.assessment.dto.request.CreateAssessmentRequest;
import careerpilot_parent.assessment.dto.request.UpdateAssessmentRequest;
import careerpilot_parent.assessment.dto.request.UpdateAssessmentResultRequest;
import careerpilot_parent.assessment.dto.request.UpdateAssessmentStatusRequest;
import careerpilot_parent.assessment.dto.response.AssessmentResponse;
import careerpilot_parent.assessment.entity.Assessment;
import careerpilot_parent.assessment.enums.AssessmentMode;
import careerpilot_parent.assessment.enums.AssessmentProvider;
import careerpilot_parent.assessment.enums.AssessmentResult;
import careerpilot_parent.assessment.enums.AssessmentStatus;
import careerpilot_parent.assessment.mapper.AssessmentMapper;
import careerpilot_parent.assessment.repository.AssessmentRepository;
import careerpilot_parent.assessment.service.RecruiterAssessmentService;
import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.company.entity.RecruiterProfile;
import careerpilot_parent.company.repository.RecruiterProfileRepository;
import careerpilot_parent.job.entity.JobApplication;
import careerpilot_parent.job.repository.JobApplicationRepository;
import careerpilot_parent.security.util.SecurityUtils;
import careerpilot_parent.shared.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RecruiterAssessmentServiceImpl
        implements RecruiterAssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final AssessmentMapper assessmentMapper;
    private final SecurityUtils securityUtils;

    private static final Set<AssessmentStatus> NON_EDITABLE_STATUSES =
            EnumSet.of(
                    AssessmentStatus.COMPLETED,
                    AssessmentStatus.CANCELLED,
                    AssessmentStatus.EXPIRED
            );

    @Override
    public AssessmentResponse createAssessment(
            Long applicationId,
            CreateAssessmentRequest request
    ) {

        RecruiterProfile recruiter =
                getCurrentActiveRecruiter();

        JobApplication application =
                getRecruiterApplication(
                        applicationId,
                        recruiter.getId()
                );

        validateApplicationCanHaveAssessment(
                application
        );

        validateAssessmentRequest(
                request.getAssessmentMode(),
                request.getProvider(),
                request.getAssessmentUrl(),
                request.getScheduledAt(),
                request.getAvailableUntil(),
                request.getMaximumScore(),
                request.getPassingScore()
        );

        Assessment assessment =
                assessmentMapper.toEntity(request);

        assessment.setJobApplication(application);
        assessment.setRecruiter(recruiter);

        Assessment savedAssessment =
                assessmentRepository.save(assessment);

        /*
         * Keep the job application workflow synchronized
         * with assessment scheduling.
         */
        if (application.getStatus()
                != ApplicationStatus.ASSESSMENT_SCHEDULED) {

            application.setStatus(
                    ApplicationStatus.ASSESSMENT_SCHEDULED
            );

            application.setLastStatusChangedAt(
                    LocalDateTime.now()
            );

            jobApplicationRepository.save(application);
        }

        return assessmentMapper.toResponse(
                savedAssessment
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssessmentResponse> getMyAssessments(
            AssessmentStatus status,
            Pageable pageable
    ) {

        RecruiterProfile recruiter =
                getCurrentActiveRecruiter();

        Page<Assessment> assessments;

        if (status == null) {

            assessments =
                    assessmentRepository.findByRecruiterId(
                            recruiter.getId(),
                            pageable
                    );

        } else {

            assessments =
                    assessmentRepository
                            .findByRecruiterIdAndStatus(
                                    recruiter.getId(),
                                    status,
                                    pageable
                            );
        }

        return assessments.map(
                assessmentMapper::toResponse
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssessmentResponse>
    getAssessmentsForApplication(
            Long applicationId,
            Pageable pageable
    ) {

        RecruiterProfile recruiter =
                getCurrentActiveRecruiter();

        getRecruiterApplication(
                applicationId,
                recruiter.getId()
        );

        return assessmentRepository
                .findByJobApplicationId(
                        applicationId,
                        pageable
                )
                .map(assessmentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AssessmentResponse getAssessmentById(
            Long assessmentId
    ) {

        RecruiterProfile recruiter =
                getCurrentActiveRecruiter();

        Assessment assessment =
                getRecruiterAssessment(
                        assessmentId,
                        recruiter.getId()
                );

        return assessmentMapper.toResponse(
                assessment
        );
    }

    @Override
    public AssessmentResponse updateAssessment(
            Long assessmentId,
            UpdateAssessmentRequest request
    ) {

        RecruiterProfile recruiter =
                getCurrentActiveRecruiter();

        Assessment assessment =
                getRecruiterAssessment(
                        assessmentId,
                        recruiter.getId()
                );

        validateAssessmentEditable(assessment);

        /*
         * AssessmentMode is intentionally not changed by
         * UpdateAssessmentRequest. Existing mode remains unchanged.
         */
        validateAssessmentRequest(
                assessment.getAssessmentMode(),
                request.getProvider(),
                request.getAssessmentUrl(),
                request.getScheduledAt(),
                request.getAvailableUntil(),
                request.getMaximumScore(),
                request.getPassingScore()
        );

        /*
         * Do not allow maximum score to be reduced below
         * an already recorded obtained score.
         */
        if (assessment.getObtainedScore() != null
                && request.getMaximumScore() != null
                && assessment.getObtainedScore()
                > request.getMaximumScore()) {

            throw new IllegalArgumentException(
                    "Maximum score cannot be less than the already obtained score."
            );
        }

        assessmentMapper.updateEntity(
                request,
                assessment
        );

        Assessment updatedAssessment =
                assessmentRepository.save(assessment);

        return assessmentMapper.toResponse(
                updatedAssessment
        );
    }

    @Override
    public AssessmentResponse updateAssessmentStatus(
            Long assessmentId,
            UpdateAssessmentStatusRequest request
    ) {

        RecruiterProfile recruiter =
                getCurrentActiveRecruiter();

        Assessment assessment =
                getRecruiterAssessment(
                        assessmentId,
                        recruiter.getId()
                );

        AssessmentStatus currentStatus =
                assessment.getStatus();

        AssessmentStatus newStatus =
                request.getStatus();

        if (currentStatus == newStatus) {
            throw new IllegalStateException(
                    "Assessment is already in "
                            + newStatus
                            + " status."
            );
        }

        validateAssessmentStatusTransition(
                currentStatus,
                newStatus
        );

        LocalDateTime now =
                LocalDateTime.now();

        assessment.setStatus(newStatus);

        switch (newStatus) {

            case STARTED ->
                    assessment.setStartedAt(now);

            case SUBMITTED ->
                    assessment.setSubmittedAt(now);

            case COMPLETED -> {

                assessment.setCompletedAt(now);

                if (assessment.getResult()
                        == AssessmentResult.PENDING) {

                    throw new IllegalStateException(
                            "Assessment result must be updated before marking the assessment as completed."
                    );
                }
            }

            case CANCELLED ->
                    assessment.setCancelledAt(now);

            default -> {
                // No additional timestamp required.
            }
        }

        Assessment savedAssessment =
                assessmentRepository.save(assessment);

        synchronizeApplicationStatus(
                assessment.getJobApplication(),
                newStatus
        );

        return assessmentMapper.toResponse(
                savedAssessment
        );
    }

    @Override
    public AssessmentResponse updateAssessmentResult(
            Long assessmentId,
            UpdateAssessmentResultRequest request
    ) {

        RecruiterProfile recruiter =
                getCurrentActiveRecruiter();

        Assessment assessment =
                getRecruiterAssessment(
                        assessmentId,
                        recruiter.getId()
                );

        if (assessment.getStatus()
                == AssessmentStatus.CANCELLED) {

            throw new IllegalStateException(
                    "Result cannot be updated for a cancelled assessment."
            );
        }

        if (assessment.getStatus()
                == AssessmentStatus.EXPIRED) {

            throw new IllegalStateException(
                    "Result cannot be updated for an expired assessment."
            );
        }

        validateObtainedScore(
                request.getObtainedScore(),
                assessment.getMaximumScore()
        );

        AssessmentResult calculatedResult =
                calculateAssessmentResult(
                        request.getObtainedScore(),
                        assessment.getPassingScore(),
                        request.getResult()
                );

        assessment.setObtainedScore(
                request.getObtainedScore()
        );

        assessment.setResult(calculatedResult);

        assessment.setResultNotes(
                normalizeText(
                        request.getResultNotes()
                )
        );

        assessment.setStatus(
                AssessmentStatus.COMPLETED
        );

        assessment.setCompletedAt(
                LocalDateTime.now()
        );

        Assessment savedAssessment =
                assessmentRepository.save(assessment);

        JobApplication application =
                assessment.getJobApplication();

        application.setStatus(
                ApplicationStatus.ASSESSMENT_COMPLETED
        );

        application.setLastStatusChangedAt(
                LocalDateTime.now()
        );

        jobApplicationRepository.save(application);

        return assessmentMapper.toResponse(
                savedAssessment
        );
    }

    @Override
    public void deleteAssessment(
            Long assessmentId
    ) {

        RecruiterProfile recruiter =
                getCurrentActiveRecruiter();

        Assessment assessment =
                getRecruiterAssessment(
                        assessmentId,
                        recruiter.getId()
                );

        validateAssessmentEditable(assessment);

        if (assessment.getStatus()
                == AssessmentStatus.STARTED
                || assessment.getStatus()
                == AssessmentStatus.SUBMITTED) {

            throw new IllegalStateException(
                    "An assessment that has been started or submitted cannot be deleted."
            );
        }

        assessmentRepository.delete(assessment);
    }

    private RecruiterProfile getCurrentActiveRecruiter() {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        return recruiterProfileRepository
                .findByUserIdAndActiveTrue(
                        currentUserId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active recruiter profile not found."
                        )
                );
    }

    private JobApplication getRecruiterApplication(
            Long applicationId,
            Long recruiterId
    ) {

        JobApplication application =
                jobApplicationRepository
                        .findById(applicationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job application not found."
                                )
                        );

        if (application.getJobPosting() == null
                || application.getJobPosting()
                .getRecruiter() == null
                || !application.getJobPosting()
                .getRecruiter()
                .getId()
                .equals(recruiterId)) {

            throw new ResourceNotFoundException(
                    "Job application not found or does not belong to the current recruiter."
            );
        }

        return application;
    }

    private Assessment getRecruiterAssessment(
            Long assessmentId,
            Long recruiterId
    ) {

        return assessmentRepository
                .findByIdAndRecruiterId(
                        assessmentId,
                        recruiterId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Assessment not found or does not belong to the current recruiter."
                        )
                );
    }

    private void validateApplicationCanHaveAssessment(
            JobApplication application
    ) {

        if (application.getStatus() == null) {
            throw new IllegalStateException(
                    "Job application status is missing."
            );
        }

        if (application.getStatus()
                == ApplicationStatus.WITHDRAWN) {

            throw new IllegalStateException(
                    "Assessment cannot be created because the application has been withdrawn."
            );
        }

        if (application.getStatus()
                == ApplicationStatus.REJECTED) {

            throw new IllegalStateException(
                    "Assessment cannot be created for a rejected application."
            );
        }

        if (application.getStatus()
                == ApplicationStatus.HIRED) {

            throw new IllegalStateException(
                    "Assessment cannot be created because the candidate has already been hired."
            );
        }
    }

    private void validateAssessmentRequest(
            AssessmentMode mode,
            AssessmentProvider provider,
            String assessmentUrl,
            LocalDateTime scheduledAt,
            LocalDateTime availableUntil,
            Double maximumScore,
            Double passingScore
    ) {

        if (scheduledAt == null
                || availableUntil == null) {

            throw new IllegalArgumentException(
                    "Scheduled time and availability end time are required."
            );
        }

        if (!scheduledAt.isBefore(
                availableUntil
        )) {

            throw new IllegalArgumentException(
                    "Scheduled time must be before the assessment availability end time."
            );
        }

        if (maximumScore == null
                || maximumScore <= 0) {

            throw new IllegalArgumentException(
                    "Maximum score must be greater than zero."
            );
        }

        if (passingScore == null
                || passingScore < 0) {

            throw new IllegalArgumentException(
                    "Passing score cannot be negative."
            );
        }

        if (passingScore > maximumScore) {

            throw new IllegalArgumentException(
                    "Passing score cannot be greater than maximum score."
            );
        }

        if (mode == AssessmentMode.EXTERNAL_LINK) {

            if (assessmentUrl == null
                    || assessmentUrl.isBlank()) {

                throw new IllegalArgumentException(
                        "Assessment URL is required for EXTERNAL_LINK mode."
                );
            }

            if (provider
                    == AssessmentProvider.CAREERPILOT) {

                throw new IllegalArgumentException(
                        "CAREERPILOT provider cannot be used for an external-link assessment."
                );
            }
        }

        if (mode == AssessmentMode.INTERNAL_CODING
                || mode == AssessmentMode.INTERNAL_QUIZ) {

            if (provider
                    != AssessmentProvider.CAREERPILOT) {

                throw new IllegalArgumentException(
                        "CAREERPILOT provider is required for internal assessments."
                );
            }

            if (assessmentUrl != null
                    && !assessmentUrl.isBlank()) {

                throw new IllegalArgumentException(
                        "External assessment URL must not be provided for an internal assessment."
                );
            }
        }
    }

    private void validateAssessmentEditable(
            Assessment assessment
    ) {

        if (NON_EDITABLE_STATUSES.contains(
                assessment.getStatus()
        )) {

            throw new IllegalStateException(
                    "Assessment cannot be edited because its status is "
                            + assessment.getStatus()
                            + "."
            );
        }

        if (assessment.getCompletedAt() != null) {

            throw new IllegalStateException(
                    "Completed assessment cannot be edited."
            );
        }
    }

    private void validateObtainedScore(
            Double obtainedScore,
            Double maximumScore
    ) {

        if (obtainedScore == null) {
            throw new IllegalArgumentException(
                    "Obtained score is required."
            );
        }

        if (obtainedScore < 0) {
            throw new IllegalArgumentException(
                    "Obtained score cannot be negative."
            );
        }

        if (maximumScore == null) {
            throw new IllegalStateException(
                    "Maximum assessment score is missing."
            );
        }

        if (obtainedScore > maximumScore) {

            throw new IllegalArgumentException(
                    "Obtained score cannot be greater than maximum score."
            );
        }
    }

    private AssessmentResult calculateAssessmentResult(
            Double obtainedScore,
            Double passingScore,
            AssessmentResult requestedResult
    ) {

        if (requestedResult
                == AssessmentResult.DISQUALIFIED
                || requestedResult
                == AssessmentResult.NOT_ATTEMPTED) {

            return requestedResult;
        }

        AssessmentResult calculatedResult =
                obtainedScore >= passingScore
                        ? AssessmentResult.PASSED
                        : AssessmentResult.FAILED;

        if (requestedResult != null
                && requestedResult
                != AssessmentResult.PENDING
                && requestedResult
                != calculatedResult) {

            throw new IllegalArgumentException(
                    "Assessment result does not match the obtained score and passing score."
            );
        }

        return calculatedResult;
    }

    private void validateAssessmentStatusTransition(
            AssessmentStatus currentStatus,
            AssessmentStatus newStatus
    ) {

        boolean validTransition =
                switch (currentStatus) {

                    case DRAFT ->
                            newStatus
                                    == AssessmentStatus.SCHEDULED
                                    || newStatus
                                    == AssessmentStatus.CANCELLED;

                    case SCHEDULED ->
                            newStatus
                                    == AssessmentStatus.AVAILABLE
                                    || newStatus
                                    == AssessmentStatus.STARTED
                                    || newStatus
                                    == AssessmentStatus.CANCELLED
                                    || newStatus
                                    == AssessmentStatus.EXPIRED
                                    || newStatus
                                    == AssessmentStatus.MISSED;

                    case AVAILABLE ->
                            newStatus
                                    == AssessmentStatus.STARTED
                                    || newStatus
                                    == AssessmentStatus.CANCELLED
                                    || newStatus
                                    == AssessmentStatus.EXPIRED
                                    || newStatus
                                    == AssessmentStatus.MISSED;

                    case STARTED ->
                            newStatus
                                    == AssessmentStatus.SUBMITTED
                                    || newStatus
                                    == AssessmentStatus.CANCELLED;

                    case SUBMITTED ->
                            newStatus
                                    == AssessmentStatus.COMPLETED;

                    case COMPLETED,
                         CANCELLED,
                         EXPIRED,
                         MISSED -> false;
                };

        if (!validTransition) {

            throw new IllegalStateException(
                    "Invalid assessment status transition from "
                            + currentStatus
                            + " to "
                            + newStatus
                            + "."
            );
        }
    }

    private void synchronizeApplicationStatus(
            JobApplication application,
            AssessmentStatus assessmentStatus
    ) {

        if (application == null) {
            return;
        }

        if (assessmentStatus
                == AssessmentStatus.SCHEDULED
                || assessmentStatus
                == AssessmentStatus.AVAILABLE
                || assessmentStatus
                == AssessmentStatus.STARTED
                || assessmentStatus
                == AssessmentStatus.SUBMITTED) {

            application.setStatus(
                    ApplicationStatus.ASSESSMENT_SCHEDULED
            );

            application.setLastStatusChangedAt(
                    LocalDateTime.now()
            );

            jobApplicationRepository.save(application);
        }

        if (assessmentStatus
                == AssessmentStatus.COMPLETED) {

            application.setStatus(
                    ApplicationStatus.ASSESSMENT_COMPLETED
            );

            application.setLastStatusChangedAt(
                    LocalDateTime.now()
            );

            jobApplicationRepository.save(application);
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