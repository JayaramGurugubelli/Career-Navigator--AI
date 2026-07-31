package career_Navigator_parent.savedjob.service.impl;

import career_Navigator_parent.common.exception.ResourceNotFoundException;

import career_Navigator_parent.job.entity.JobPosting;
import career_Navigator_parent.job.repository.JobApplicationRepository;
import career_Navigator_parent.job.repository.JobPostingRepository;

import career_Navigator_parent.savedjob.dto.response.SavedJobHistoryResponse;
import career_Navigator_parent.savedjob.dto.response.SavedJobResponse;
import career_Navigator_parent.savedjob.dto.response.SavedJobStatusResponse;

import career_Navigator_parent.savedjob.entity.SavedJob;
import career_Navigator_parent.savedjob.entity.SavedJobHistory;

import career_Navigator_parent.savedjob.enums.SavedJobAction;

import career_Navigator_parent.savedjob.mapper.SavedJobMapper;

import career_Navigator_parent.savedjob.repository.SavedJobHistoryRepository;
import career_Navigator_parent.savedjob.repository.SavedJobRepository;

import career_Navigator_parent.savedjob.service.SavedJobService;

import career_Navigator_parent.security.util.SecurityUtils;

import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SavedJobServiceImpl
        implements SavedJobService {

    private static final Set<String>
            SAVEABLE_JOB_STATUSES =
            Set.of(
                    "PUBLISHED",
                    "OPEN",
                    "ACTIVE"
            );

    private final SavedJobRepository
            savedJobRepository;

    private final SavedJobHistoryRepository
            savedJobHistoryRepository;

    private final JobPostingRepository
            jobPostingRepository;

    private final JobApplicationRepository
            jobApplicationRepository;

    private final StudentRepository
            studentRepository;

    private final SavedJobMapper
            savedJobMapper;

    private final SecurityUtils
            securityUtils;

    @Override
    @Transactional
    public SavedJobResponse saveJob(
            Long jobId
    ) {

        validateId(jobId, "Job ID");

        Student student =
                getCurrentStudent();

        JobPosting jobPosting =
                getJobPosting(jobId);

        validateJobCanBeSaved(jobPosting);

        boolean alreadySaved =
                savedJobRepository
                        .existsByStudentIdAndJobPostingId(
                                student.getId(),
                                jobId
                        );

        if (alreadySaved) {
            throw new IllegalStateException(
                    "This job is already saved."
            );
        }

        SavedJob savedJob =
                SavedJob.builder()
                        .student(student)
                        .jobPosting(jobPosting)
                        .savedAt(LocalDateTime.now())
                        .build();

        try {
            SavedJob persistedSavedJob =
                    savedJobRepository.saveAndFlush(
                            savedJob
                    );

            saveHistory(
                    student,
                    jobPosting,
                    SavedJobAction.SAVED
            );

            boolean applied =
                    jobApplicationRepository
                            .existsByStudentIdAndJobPostingId(
                                    student.getId(),
                                    jobId
                            );

            return savedJobMapper.toResponse(
                    persistedSavedJob,
                    applied
            );

        } catch (DataIntegrityViolationException exception) {

            throw new IllegalStateException(
                    "This job is already saved."
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SavedJobResponse> getSavedJobs(
            String keyword,
            String location,
            Pageable pageable
    ) {

        Student student =
                getCurrentStudent();

        String normalizedKeyword =
                normalizeText(keyword);

        String normalizedLocation =
                normalizeText(location);

        return savedJobRepository
                .searchStudentSavedJobs(
                        student.getId(),
                        normalizedKeyword,
                        normalizedLocation,
                        pageable
                )
                .map(savedJob -> {

                    Long jobId =
                            savedJob
                                    .getJobPosting()
                                    .getId();

                    boolean applied =
                            jobApplicationRepository
                                    .existsByStudentIdAndJobPostingId(
                                            student.getId(),
                                            jobId
                                    );

                    return savedJobMapper.toResponse(
                            savedJob,
                            applied
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public SavedJobStatusResponse getSavedJobStatus(
            Long jobId
    ) {

        validateId(jobId, "Job ID");

        Student student =
                getCurrentStudent();

        SavedJob savedJob =
                savedJobRepository
                        .findByStudentIdAndJobPostingId(
                                student.getId(),
                                jobId
                        )
                        .orElse(null);

        return savedJobMapper.toStatusResponse(
                jobId,
                savedJob
        );
    }

    @Override
    @Transactional
    public void removeSavedJob(
            Long jobId
    ) {

        validateId(jobId, "Job ID");

        Student student =
                getCurrentStudent();

        SavedJob savedJob =
                savedJobRepository
                        .findByStudentIdAndJobPostingId(
                                student.getId(),
                                jobId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Saved job not found."
                                        )
                        );

        JobPosting jobPosting =
                savedJob.getJobPosting();

        /*
         * Write the permanent audit record before deleting
         * the current bookmark.
         */
        saveHistory(
                student,
                jobPosting,
                SavedJobAction.REMOVED
        );

        savedJobRepository.delete(savedJob);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SavedJobHistoryResponse>
    getSavedJobHistory(
            SavedJobAction action,
            Pageable pageable
    ) {

        Student student =
                getCurrentStudent();

        return savedJobHistoryRepository
                .findStudentHistory(
                        student.getId(),
                        action,
                        pageable
                )
                .map(
                        savedJobMapper::toHistoryResponse
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

    private JobPosting getJobPosting(
            Long jobId
    ) {

        return jobPostingRepository
                .findById(jobId)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Job posting not found."
                                )
                );
    }

    private void validateJobCanBeSaved(
            JobPosting jobPosting
    ) {

        if (jobPosting.getStatus() == null) {
            throw new IllegalStateException(
                    "Job posting is not available."
            );
        }

        String status =
                jobPosting
                        .getStatus()
                        .name()
                        .trim()
                        .toUpperCase(Locale.ROOT);

        if (!SAVEABLE_JOB_STATUSES.contains(status)) {
            throw new IllegalStateException(
                    "Only currently published jobs can be saved."
            );
        }

        if (jobPosting.getApplicationDeadline() != null
                && jobPosting
                .getApplicationDeadline()
                .isBefore(
                        java.time.LocalDate.now()
                )) {

            throw new IllegalStateException(
                    "This job's application deadline has passed."
            );
        }
    }

    private void saveHistory(
            Student student,
            JobPosting jobPosting,
            SavedJobAction action
    ) {

        String companyName =
                jobPosting.getCompany() == null
                        ? "Unknown company"
                        : jobPosting
                        .getCompany()
                        .getName();

        SavedJobHistory history =
                SavedJobHistory.builder()
                        .student(student)
                        .jobPosting(jobPosting)
                        .action(action)
                        .jobTitleSnapshot(
                                jobPosting.getTitle()
                        )
                        .companyNameSnapshot(
                                companyName
                        )
                        .locationSnapshot(
                                normalizeText(
                                        jobPosting.getLocation()
                                )
                        )
                        .actionAt(LocalDateTime.now())
                        .build();

        savedJobHistoryRepository.save(history);
    }

    private void validateId(
            Long id,
            String fieldName
    ) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be positive."
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