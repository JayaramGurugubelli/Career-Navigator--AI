package careerpilot_parent.admin.service.impl;

import careerpilot_parent.admin.dto.request.UpdateJobStatusRequest;
import careerpilot_parent.admin.dto.response.AdminJobResponse;
import careerpilot_parent.admin.mapper.AdminMapper;
import careerpilot_parent.admin.service.AdminJobService;
import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.company.enums.JobStatus;
import careerpilot_parent.job.entity.JobPosting;
import careerpilot_parent.job.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminJobServiceImpl implements AdminJobService {

    private final JobPostingRepository
            jobPostingRepository;

    private final AdminMapper adminMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<AdminJobResponse> getJobs(
            JobStatus status,
            String keyword,
            Pageable pageable
    ) {

        List<JobPosting> filtered =
                jobPostingRepository.findAll()
                        .stream()
                        .filter(job ->
                                status == null ||
                                job.getStatus() == status)
                        .filter(job ->
                                matchesKeyword(job, keyword))
                        .toList();

        int start = Math.min(
                (int) pageable.getOffset(),
                filtered.size()
        );

        int end = Math.min(
                start + pageable.getPageSize(),
                filtered.size()
        );

        List<AdminJobResponse> content =
                filtered.subList(start, end)
                        .stream()
                        .map(adminMapper::toJobResponse)
                        .toList();

        return new PageImpl<>(
                content,
                pageable,
                filtered.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AdminJobResponse getJobById(
            Long jobId
    ) {

        return adminMapper.toJobResponse(
                getJob(jobId)
        );
    }

    @Override
    public AdminJobResponse updateJobStatus(
            Long jobId,
            UpdateJobStatusRequest request
    ) {

        JobPosting job = getJob(jobId);

        if (job.getStatus() ==
                request.getStatus()) {

            throw new IllegalStateException(
                    "Job is already in "
                            + request.getStatus()
                            + " status."
            );
        }

        job.setStatus(request.getStatus());

        return adminMapper.toJobResponse(
                jobPostingRepository.save(job)
        );
    }

    @Override
    public void deleteJob(Long jobId) {

        JobPosting job = getJob(jobId);

        jobPostingRepository.delete(job);
    }

    private JobPosting getJob(Long jobId) {

        return jobPostingRepository
                .findById(jobId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Job posting not found."
                        )
                );
    }

    private boolean matchesKeyword(
            JobPosting job,
            String keyword
    ) {

        if (keyword == null ||
                keyword.isBlank()) {
            return true;
        }

        String value =
                keyword.trim().toLowerCase();

        return contains(job.getTitle(), value)
                || contains(job.getLocation(), value)
                || contains(
                        job.getCompany().getName(),
                        value
                );
    }

    private boolean contains(
            String source,
            String value
    ) {

        return source != null &&
                source.toLowerCase().contains(value);
    }
}
