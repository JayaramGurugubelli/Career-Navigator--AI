package career_Navigator_parent.job.service.impl;

import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.company.entity.RecruiterProfile;
import career_Navigator_parent.company.repository.RecruiterProfileRepository;
import career_Navigator_parent.job.dto.response.ApplicationStatusHistoryResponse;
import career_Navigator_parent.job.entity.ApplicationStatusHistory;
import career_Navigator_parent.job.entity.JobApplication;
import career_Navigator_parent.job.mapper.ApplicationStatusHistoryMapper;
import career_Navigator_parent.job.repository.ApplicationStatusHistoryRepository;
import career_Navigator_parent.job.repository.JobApplicationRepository;
import career_Navigator_parent.job.service.ApplicationStatusHistoryService;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.shared.enums.ApplicationStatus;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;
import career_Navigator_parent.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationStatusHistoryServiceImpl
        implements ApplicationStatusHistoryService {

    private final ApplicationStatusHistoryRepository
            applicationStatusHistoryRepository;
    private final JobApplicationRepository
            jobApplicationRepository;
    private final ApplicationStatusHistoryMapper
            applicationStatusHistoryMapper;
    private final StudentRepository studentRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public ApplicationStatusHistoryResponse recordStatusChange(
            JobApplication application,
            ApplicationStatus previousStatus,
            ApplicationStatus newStatus,
            User changedBy,
            String comment
    ) {
        validateApplication(application);

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "New application status is required."
            );
        }

        if (Objects.equals(previousStatus, newStatus)) {
            throw new IllegalArgumentException(
                    "New status must be different from the previous status."
            );
        }

        ApplicationStatusHistory history =
                ApplicationStatusHistory.builder()
                        .application(application)
                        .previousStatus(previousStatus)
                        .newStatus(newStatus)
                        .changedBy(changedBy)
                        .comment(normalizeComment(comment))
                        .build();

        return applicationStatusHistoryMapper.toResponse(
                applicationStatusHistoryRepository.save(history)
        );
    }

    @Override
    public List<ApplicationStatusHistoryResponse> getApplicationHistory(
            Long applicationId
    ) {
        validateApplicationId(applicationId);

        if (!jobApplicationRepository.existsById(applicationId)) {
            throw new ResourceNotFoundException(
                    "Job application not found with ID: " + applicationId
            );
        }

        return mapResponses(
                applicationStatusHistoryRepository
                        .findByApplication_IdOrderByCreatedAtAsc(applicationId)
        );
    }

    @Override
    public List<ApplicationStatusHistoryResponse>
    getStudentApplicationHistory(Long applicationId) {
        Student student = getCurrentStudent();

        if (!jobApplicationRepository.existsByIdAndStudentId(
                applicationId,
                student.getId()
        )) {
            throw new ResourceNotFoundException(
                    "Application not found or does not belong to the current student."
            );
        }

        return mapResponses(
                getStudentApplicationHistoryEntities(
                        applicationId,
                        student.getId()
                )
        );
    }

    @Override
    public List<ApplicationStatusHistoryResponse>
    getRecruiterApplicationHistory(Long applicationId) {
        RecruiterProfile recruiter = getCurrentRecruiter();

        if (jobApplicationRepository
                .findByIdAndJobPostingRecruiterId(
                        applicationId,
                        recruiter.getId()
                )
                .isEmpty()) {
            throw new ResourceNotFoundException(
                    "Application not found or does not belong to the current recruiter."
            );
        }

        return mapResponses(
                applicationStatusHistoryRepository
                        .findRecruiterApplicationHistory(
                                applicationId,
                                recruiter.getId()
                        )
        );
    }

    @Override
    public List<ApplicationStatusHistory>
    getStudentApplicationHistoryEntities(
            Long applicationId,
            Long studentId
    ) {
        return applicationStatusHistoryRepository
                .findStudentApplicationHistory(
                        applicationId,
                        studentId
                );
    }

    private List<ApplicationStatusHistoryResponse> mapResponses(
            List<ApplicationStatusHistory> histories
    ) {
        return histories.stream()
                .map(applicationStatusHistoryMapper::toResponse)
                .toList();
    }

    private void validateApplication(JobApplication application) {
        if (application == null || application.getId() == null) {
            throw new IllegalArgumentException(
                    "A saved job application is required."
            );
        }

        if (!jobApplicationRepository.existsById(application.getId())) {
            throw new ResourceNotFoundException(
                    "Job application not found with ID: "
                            + application.getId()
            );
        }
    }

    private void validateApplicationId(Long applicationId) {
        if (applicationId == null || applicationId <= 0) {
            throw new IllegalArgumentException(
                    "Application ID must be positive."
            );
        }
    }

    private Student getCurrentStudent() {
        Long userId = securityUtils.getCurrentUserId();

        return studentRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student profile not found."
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

    private String normalizeComment(String comment) {
        return comment == null || comment.isBlank()
                ? null
                : comment.trim();
    }
}
