package careerpilot_parent.recruiter.service;

import careerpilot_parent.company.enums.ApplicationStatus;
import careerpilot_parent.job.dto.request.UpdateJobApplicationStatusRequest;
import careerpilot_parent.job.dto.request.UpdateRecruiterNotesRequest;
import careerpilot_parent.job.dto.response.JobApplicationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruiterJobApplicationService {

    Page<JobApplicationResponse> getApplicationsForJob(
            Long jobId,
            ApplicationStatus status,
            Pageable pageable
    );

    JobApplicationResponse getApplicationById(
            Long applicationId
    );

    JobApplicationResponse updateApplicationStatus(
            Long applicationId,
            UpdateJobApplicationStatusRequest request
    );

    JobApplicationResponse updateRecruiterNotes(
            Long applicationId,
            UpdateRecruiterNotesRequest request
    );
}