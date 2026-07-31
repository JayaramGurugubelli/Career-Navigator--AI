package career_Navigator_parent.job.service;

import career_Navigator_parent.job.dto.request.CreateJobApplicationRequest;
import career_Navigator_parent.job.dto.response.JobApplicationResponse;

import career_Navigator_parent.shared.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobApplicationService {

    JobApplicationResponse applyForJob(
            Long jobId,
            CreateJobApplicationRequest request
    );

    Page<JobApplicationResponse> getMyApplications(
            ApplicationStatus status,
            Pageable pageable
    );

    JobApplicationResponse getMyApplicationById(
            Long applicationId
    );

    JobApplicationResponse withdrawApplication(
            Long applicationId,
            String reason
    );

    boolean hasApplied(
            Long jobId
    );
}