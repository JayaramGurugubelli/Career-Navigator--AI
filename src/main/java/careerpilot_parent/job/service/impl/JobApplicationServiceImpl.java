package careerpilot_parent.job.service.impl;

import careerpilot_parent.common.exception.ResourceNotFoundException;

import careerpilot_parent.company.enums.JobStatus;

import careerpilot_parent.job.dto.request.CreateJobApplicationRequest;
import careerpilot_parent.job.dto.response.JobApplicationResponse;

import careerpilot_parent.job.entity.ApplicationStatusHistory;
import careerpilot_parent.job.entity.JobApplication;
import careerpilot_parent.job.entity.JobPosting;

import careerpilot_parent.job.mapper.JobMapper;

import careerpilot_parent.job.repository.ApplicationStatusHistoryRepository;
import careerpilot_parent.job.repository.JobApplicationRepository;
import careerpilot_parent.job.repository.JobPostingRepository;

import careerpilot_parent.job.service.JobApplicationService;

import careerpilot_parent.resume.entity.Resume;
import careerpilot_parent.resume.repository.ResumeRepository;

import careerpilot_parent.security.util.SecurityUtils;

import careerpilot_parent.shared.enums.ApplicationStatus;
import careerpilot_parent.student.entity.Student;
import careerpilot_parent.student.repository.StudentRepository;

import careerpilot_parent.user.entity.User;
import careerpilot_parent.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository
            jobApplicationRepository;

    private final JobPostingRepository
            jobPostingRepository;

    private final ApplicationStatusHistoryRepository
            applicationStatusHistoryRepository;

    private final StudentRepository studentRepository;

    private final ResumeRepository resumeRepository;

    private final UserRepository userRepository;

    private final JobMapper jobMapper;

    private final SecurityUtils securityUtils;

    @Override
    public JobApplicationResponse applyForJob(
            Long jobId,
            CreateJobApplicationRequest request
    ) {

        Student student =
                getCurrentStudent();

        User currentUser =
                getCurrentUser();

        JobPosting jobPosting =
                getAvailableJob(jobId);

        validateApplicationDeadline(
                jobPosting
        );

        validateDuplicateApplication(
                student.getId(),
                jobPosting.getId()
        );

        Resume resume =
                resumeRepository
                        .findByIdAndStudentId(
                                request.getResumeId(),
                                student.getId()
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Resume not found or does not belong to the current student."
                                        )
                        );

        LocalDateTime now =
                LocalDateTime.now();

        JobApplication application =
                JobApplication.builder()
                        .jobPosting(jobPosting)
                        .student(student)
                        .resume(resume)
                        .coverLetter(
                                normalizeText(
                                        request.getCoverLetter()
                                )
                        )
                        .status(
                                ApplicationStatus.SUBMITTED
                        )
                        .appliedAt(now)
                        .lastStatusChangedAt(now)
                        .build();

        JobApplication savedApplication =
                jobApplicationRepository.save(
                        application
                );

        saveStatusHistory(
                savedApplication,
                null,
                ApplicationStatus.SUBMITTED,
                currentUser,
                "Application submitted by student."
        );

        Long currentCount =
                jobPosting.getApplicationCount();

        jobPosting.setApplicationCount(
                currentCount == null
                        ? 1L
                        : currentCount + 1L
        );

        jobPostingRepository.save(
                jobPosting
        );

        return jobMapper.toResponse(
                savedApplication
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse>
    getMyApplications(
            ApplicationStatus status,
            Pageable pageable
    ) {

        Student student =
                getCurrentStudent();

        Page<JobApplication> applications;

        if (status == null) {

            applications =
                    jobApplicationRepository
                            .findByStudentId(
                                    student.getId(),
                                    pageable
                            );

        } else {

            applications =
                    jobApplicationRepository
                            .findByStudentIdAndStatus(
                                    student.getId(),
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
    public JobApplicationResponse
    getMyApplicationById(
            Long applicationId
    ) {

        Student student =
                getCurrentStudent();

        JobApplication application =
                getStudentApplication(
                        applicationId,
                        student.getId()
                );

        return jobMapper.toResponse(
                application
        );
    }

    @Override
    public JobApplicationResponse
    withdrawApplication(
            Long applicationId,
            String reason
    ) {

        Student student =
                getCurrentStudent();

        User currentUser =
                getCurrentUser();

        JobApplication application =
                getStudentApplication(
                        applicationId,
                        student.getId()
                );

        ApplicationStatus oldStatus =
                application.getStatus();

        validateWithdrawal(oldStatus);

        LocalDateTime now =
                LocalDateTime.now();

        application.setStatus(
                ApplicationStatus.WITHDRAWN
        );

        application.setWithdrawnAt(now);
        application.setLastStatusChangedAt(now);

        JobApplication updatedApplication =
                jobApplicationRepository.save(
                        application
                );

        String comment =
                reason == null || reason.isBlank()
                        ? "Application withdrawn by student."
                        : "Application withdrawn by student. Reason: "
                        + reason.trim();

        saveStatusHistory(
                updatedApplication,
                oldStatus,
                ApplicationStatus.WITHDRAWN,
                currentUser,
                comment
        );

        return jobMapper.toResponse(
                updatedApplication
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasApplied(
            Long jobId
    ) {

        Student student =
                getCurrentStudent();

        return jobApplicationRepository
                .existsByStudentIdAndJobPostingId(
                        student.getId(),
                        jobId
                );
    }

    private JobPosting getAvailableJob(
            Long jobId
    ) {

        JobPosting jobPosting =
                jobPostingRepository
                        .findById(jobId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Job posting not found."
                                        )
                        );

        if (jobPosting.getStatus() !=
                JobStatus.PUBLISHED) {

            throw new IllegalStateException(
                    "This job is not currently accepting applications."
            );
        }

        if (!jobPosting.getCompany().isActive()) {

            throw new IllegalStateException(
                    "This company's job postings are currently unavailable."
            );
        }

        if (!jobPosting.getRecruiter().isActive()) {

            throw new IllegalStateException(
                    "This job posting is currently unavailable."
            );
        }

        return jobPosting;
    }

    private void validateApplicationDeadline(
            JobPosting jobPosting
    ) {

        LocalDate deadline =
                jobPosting.getApplicationDeadline();

        if (deadline != null &&
                deadline.isBefore(
                        LocalDate.now()
                )) {

            jobPosting.setStatus(
                    JobStatus.EXPIRED
            );

            jobPosting.setClosedAt(
                    LocalDateTime.now()
            );

            jobPostingRepository.save(
                    jobPosting
            );

            throw new IllegalStateException(
                    "The application deadline for this job has passed."
            );
        }
    }

    private void validateDuplicateApplication(
            Long studentId,
            Long jobId
    ) {

        boolean alreadyApplied =
                jobApplicationRepository
                        .existsByStudentIdAndJobPostingId(
                                studentId,
                                jobId
                        );

        if (alreadyApplied) {

            throw new IllegalStateException(
                    "You have already applied for this job."
            );
        }
    }

    private void validateWithdrawal(
            ApplicationStatus status
    ) {

        if (status ==
                ApplicationStatus.WITHDRAWN) {

            throw new IllegalStateException(
                    "This application has already been withdrawn."
            );
        }

        if (status ==
                ApplicationStatus.REJECTED) {

            throw new IllegalStateException(
                    "A rejected application cannot be withdrawn."
            );
        }

        if (status ==
                ApplicationStatus.HIRED) {

            throw new IllegalStateException(
                    "A hired application cannot be withdrawn."
            );
        }
    }

    private JobApplication getStudentApplication(
            Long applicationId,
            Long studentId
    ) {

        return jobApplicationRepository
                .findByIdAndStudentId(
                        applicationId,
                        studentId
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Job application not found or does not belong to the current student."
                                )
                );
    }

    private void saveStatusHistory(
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
                        .comment(comment)
                        .build();

        applicationStatusHistoryRepository.save(
                history
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

    private User getCurrentUser() {

        Long userId =
                securityUtils.getCurrentUserId();

        return userRepository
                .findById(userId)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "User not found."
                                )
                );
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