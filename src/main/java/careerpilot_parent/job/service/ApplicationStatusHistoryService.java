package careerpilot_parent.job.service;

import careerpilot_parent.job.dto.response.ApplicationStatusHistoryResponse;
import careerpilot_parent.job.entity.ApplicationStatusHistory;
import careerpilot_parent.job.entity.JobApplication;
import careerpilot_parent.shared.enums.ApplicationStatus;
import careerpilot_parent.user.entity.User;

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
