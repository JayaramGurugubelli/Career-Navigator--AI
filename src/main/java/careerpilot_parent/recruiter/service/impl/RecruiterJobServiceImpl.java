package careerpilot_parent.recruiter.service.impl;

import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.company.dto.request.CreateJobPostingRequest;
import careerpilot_parent.company.dto.request.UpdateJobPostingRequest;
import careerpilot_parent.company.dto.response.JobPostingResponse;
import careerpilot_parent.company.entity.RecruiterProfile;
import careerpilot_parent.company.enums.CurrencyCode;
import careerpilot_parent.company.enums.JobStatus;
import careerpilot_parent.company.repository.RecruiterProfileRepository;
import careerpilot_parent.job.entity.JobPosting;
import careerpilot_parent.job.mapper.JobMapper;
import careerpilot_parent.job.repository.JobPostingRepository;
import careerpilot_parent.job.util.SlugGenerator;
import careerpilot_parent.recruiter.service.RecruiterJobService;
import careerpilot_parent.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RecruiterJobServiceImpl
        implements RecruiterJobService {

    private final JobPostingRepository jobRepository;
    private final RecruiterProfileRepository recruiterRepository;
    private final SecurityUtils securityUtils;
    private final JobMapper jobMapper;
    private final SlugGenerator slugGenerator;

    @Override
    public JobPostingResponse createJob(
            CreateJobPostingRequest request
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        validateJobData(
                request.getMinimumExperience(),
                request.getMaximumExperience(),
                request.getMinimumSalary(),
                request.getMaximumSalary(),
                request.isSalaryDisclosed(),
                request.getCurrency(),
                request.getApplicationDeadline()
        );

        JobPosting job =
                jobMapper.toJobEntity(request);

        job.setRecruiter(recruiter);
        job.setCompany(recruiter.getCompany());

        job.setSlug(
                generateUniqueSlug(
                        request.getTitle(),
                        recruiter.getCompany().getName(),
                        null
                )
        );

        JobPosting savedJob =
                jobRepository.save(job);

        return jobMapper.toJobResponse(savedJob);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobPostingResponse> getMyJobs(
            JobStatus status,
            Pageable pageable
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        Page<JobPosting> jobs;

        if (status == null) {
            jobs = jobRepository.findByRecruiterId(
                    recruiter.getId(),
                    pageable
            );
        } else {
            jobs = jobRepository.findByRecruiterIdAndStatus(
                    recruiter.getId(),
                    status,
                    pageable
            );
        }

        return jobs.map(jobMapper::toJobResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostingResponse getMyJobById(
            Long jobId
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        JobPosting job =
                getOwnedJob(jobId, recruiter.getId());

        return jobMapper.toJobResponse(job);
    }

    @Override
    public JobPostingResponse updateJob(
            Long jobId,
            UpdateJobPostingRequest request
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        JobPosting job =
                getOwnedJob(jobId, recruiter.getId());

        if (job.getStatus() == JobStatus.ARCHIVED) {
            throw new IllegalStateException(
                    "Archived jobs cannot be updated."
            );
        }

        if (job.getStatus() == JobStatus.CLOSED) {
            throw new IllegalStateException(
                    "Closed jobs cannot be updated."
            );
        }

        validateJobData(
                request.getMinimumExperience(),
                request.getMaximumExperience(),
                request.getMinimumSalary(),
                request.getMaximumSalary(),
                request.isSalaryDisclosed(),
                request.getCurrency(),
                request.getApplicationDeadline()
        );

        boolean titleChanged =
                !job.getTitle()
                        .trim()
                        .equalsIgnoreCase(
                                request.getTitle().trim()
                        );

        jobMapper.updateJobEntity(request, job);

        if (titleChanged) {
            job.setSlug(
                    generateUniqueSlug(
                            request.getTitle(),
                            recruiter.getCompany().getName(),
                            job.getId()
                    )
            );
        }

        JobPosting savedJob =
                jobRepository.save(job);

        return jobMapper.toJobResponse(savedJob);
    }

    @Override
    public JobPostingResponse publishJob(
            Long jobId
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        JobPosting job =
                getOwnedJob(jobId, recruiter.getId());

        if (job.getStatus() != JobStatus.DRAFT
                && job.getStatus() != JobStatus.PAUSED) {

            throw new IllegalStateException(
                    "Only draft or paused jobs can be published."
            );
        }

        validateBeforePublishing(job);

        job.setStatus(JobStatus.PUBLISHED);

        if (job.getPublishedAt() == null) {
            job.setPublishedAt(LocalDateTime.now());
        }

        job.setClosedAt(null);

        return jobMapper.toJobResponse(
                jobRepository.save(job)
        );
    }

    @Override
    public JobPostingResponse pauseJob(
            Long jobId
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        JobPosting job =
                getOwnedJob(jobId, recruiter.getId());

        if (job.getStatus() != JobStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Only published jobs can be paused."
            );
        }

        job.setStatus(JobStatus.PAUSED);

        return jobMapper.toJobResponse(
                jobRepository.save(job)
        );
    }

    @Override
    public JobPostingResponse closeJob(
            Long jobId
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        JobPosting job =
                getOwnedJob(jobId, recruiter.getId());

        if (job.getStatus() == JobStatus.CLOSED) {
            throw new IllegalStateException(
                    "Job is already closed."
            );
        }

        if (job.getStatus() == JobStatus.ARCHIVED) {
            throw new IllegalStateException(
                    "Archived jobs cannot be closed."
            );
        }

        job.setStatus(JobStatus.CLOSED);
        job.setClosedAt(LocalDateTime.now());

        return jobMapper.toJobResponse(
                jobRepository.save(job)
        );
    }

    @Override
    public void archiveJob(
            Long jobId
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        JobPosting job =
                getOwnedJob(jobId, recruiter.getId());

        if (job.getStatus() == JobStatus.ARCHIVED) {
            throw new IllegalStateException(
                    "Job is already archived."
            );
        }

        if (job.getStatus() == JobStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Pause or close the job before archiving it."
            );
        }

        job.setStatus(JobStatus.ARCHIVED);

        if (job.getClosedAt() == null) {
            job.setClosedAt(LocalDateTime.now());
        }

        jobRepository.save(job);
    }

    private void validateBeforePublishing(
            JobPosting job
    ) {

        if (job.getRequiredSkills() == null
                || job.getRequiredSkills().isEmpty()) {

            throw new IllegalStateException(
                    "At least one skill is required before publishing."
            );
        }

        if (job.getApplicationDeadline() != null
                && job.getApplicationDeadline()
                .isBefore(LocalDate.now())) {

            throw new IllegalStateException(
                    "Application deadline cannot be in the past."
            );
        }

        if (job.getCompany() == null
                || !job.getCompany().isActive()) {

            throw new IllegalStateException(
                    "Inactive companies cannot publish jobs."
            );
        }

        if (job.getRecruiter() == null
                || !job.getRecruiter().isActive()) {

            throw new IllegalStateException(
                    "Inactive recruiters cannot publish jobs."
            );
        }
    }

    private void validateJobData(
            Integer minimumExperience,
            Integer maximumExperience,
            BigDecimal minimumSalary,
            BigDecimal maximumSalary,
            boolean salaryDisclosed,
            CurrencyCode currency,
            LocalDate applicationDeadline
    ) {

        if (minimumExperience != null
                && maximumExperience != null
                && minimumExperience > maximumExperience) {

            throw new IllegalArgumentException(
                    "Minimum experience cannot exceed maximum experience."
            );
        }

        if (minimumSalary != null
                && maximumSalary != null
                && minimumSalary.compareTo(maximumSalary) > 0) {

            throw new IllegalArgumentException(
                    "Minimum salary cannot exceed maximum salary."
            );
        }

        if (salaryDisclosed
                && (minimumSalary == null
                || maximumSalary == null
                || currency == null)) {

            throw new IllegalArgumentException(
                    "Salary range and currency are required when salary is disclosed."
            );
        }

        if (applicationDeadline != null
                && applicationDeadline.isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Application deadline cannot be in the past."
            );
        }
    }

    private String generateUniqueSlug(
            String title,
            String companyName,
            Long currentJobId
    ) {

        String baseSlug =
                slugGenerator.generate(
                        title + "-" + companyName
                );

        String slug = baseSlug;
        int suffix = 1;

        while (slugBelongsToAnotherJob(
                slug,
                currentJobId
        )) {
            slug = baseSlug + "-" + suffix;
            suffix++;
        }

        return slug;
    }

    private boolean slugBelongsToAnotherJob(
            String slug,
            Long currentJobId
    ) {

        return jobRepository.findBySlug(slug)
                .map(existingJob ->
                        currentJobId == null
                                || !existingJob.getId()
                                .equals(currentJobId)
                )
                .orElse(false);
    }

    private JobPosting getOwnedJob(
            Long jobId,
            Long recruiterId
    ) {

        return jobRepository
                .findByIdAndRecruiterId(
                        jobId,
                        recruiterId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Job not found or you do not have permission to access it."
                        )
                );
    }

    private RecruiterProfile getCurrentRecruiter() {

        Long userId =
                securityUtils.getCurrentUserId();

        return recruiterRepository
                .findByUserIdAndActiveTrue(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Active recruiter profile not found."
                        )
                );
    }
}