package career_Navigator_parent.job.service;

import career_Navigator_parent.job.dto.response.ApplicationStatusHistoryResponse;
import career_Navigator_parent.job.entity.ApplicationStatusHistory;
import career_Navigator_parent.job.entity.JobApplication;
import career_Navigator_parent.shared.enums.ApplicationStatus;
import career_Navigator_parent.user.entity.User;

import java.util.List;

public interface ApplicationStatusHistoryService {

    ApplicationStatusHistoryResponse recordStatusChange(
            JobApplication application,
            ApplicationStatus previousStatus,
            ApplicationStatus newStatus,
            User changedBy,
            String comment
    );

    List<ApplicationStatusHistoryResponse> getApplicationHistory(
            Long applicationId
    );

    List<ApplicationStatusHistoryResponse> getStudentApplicationHistory(
            Long applicationId
    );

    List<ApplicationStatusHistoryResponse> getRecruiterApplicationHistory(
            Long applicationId
    );

    List<ApplicationStatusHistory> getStudentApplicationHistoryEntities(
            Long applicationId,
            Long studentId
    );
}
