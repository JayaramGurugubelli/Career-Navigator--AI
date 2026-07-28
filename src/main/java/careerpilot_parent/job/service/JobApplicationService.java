package careerpilot_parent.job.service;

import careerpilot_parent.job.dto.request.CreateJobApplicationRequest;
import careerpilot_parent.job.dto.response.JobApplicationResponse;

import careerpilot_parent.shared.enums.ApplicationStatus;
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