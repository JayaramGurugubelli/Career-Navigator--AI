package career_Navigator_parent.admin.service;

import career_Navigator_parent.admin.dto.request.UpdateJobStatusRequest;
import career_Navigator_parent.admin.dto.response.AdminJobResponse;
import career_Navigator_parent.company.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminJobService {

    Page<AdminJobResponse> getJobs(
            JobStatus status,
            String keyword,
            Pageable pageable
    );

    AdminJobResponse getJobById(Long jobId);

    AdminJobResponse updateJobStatus(
            Long jobId,
            UpdateJobStatusRequest request
    );

    void deleteJob(Long jobId);
}
