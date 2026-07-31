package career_Navigator_parent.recruiter.service;

import career_Navigator_parent.job.dto.request.UpdateJobApplicationStatusRequest;
import career_Navigator_parent.job.dto.request.UpdateRecruiterNotesRequest;
import career_Navigator_parent.job.dto.response.JobApplicationResponse;
import career_Navigator_parent.shared.enums.ApplicationStatus;
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