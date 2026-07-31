package career_Navigator_parent.recruiter.service;

import career_Navigator_parent.company.dto.request.CreateJobPostingRequest;
import career_Navigator_parent.company.dto.request.UpdateJobPostingRequest;
import career_Navigator_parent.company.dto.response.JobPostingResponse;
import career_Navigator_parent.company.enums.JobStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruiterJobService {

    JobPostingResponse createJob(
            CreateJobPostingRequest request
    );

    Page<JobPostingResponse> getMyJobs(
            JobStatus status,
            Pageable pageable
    );

    JobPostingResponse getMyJobById(
            Long jobId
    );

    JobPostingResponse updateJob(
            Long jobId,
            UpdateJobPostingRequest request
    );

    JobPostingResponse publishJob(
            Long jobId
    );

    JobPostingResponse pauseJob(
            Long jobId
    );

    JobPostingResponse closeJob(
            Long jobId
    );

    void archiveJob(
            Long jobId
    );
}