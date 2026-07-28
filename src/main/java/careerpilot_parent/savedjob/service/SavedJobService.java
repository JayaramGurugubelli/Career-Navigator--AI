package careerpilot_parent.savedjob.service;

import careerpilot_parent.savedjob.dto.response.SavedJobHistoryResponse;
import careerpilot_parent.savedjob.dto.response.SavedJobResponse;
import careerpilot_parent.savedjob.dto.response.SavedJobStatusResponse;

import careerpilot_parent.savedjob.enums.SavedJobAction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SavedJobService {

    SavedJobResponse saveJob(
            Long jobId
    );

    Page<SavedJobResponse> getSavedJobs(
            String keyword,
            String location,
            Pageable pageable
    );

    SavedJobStatusResponse getSavedJobStatus(
            Long jobId
    );

    void removeSavedJob(
            Long jobId
    );

    Page<SavedJobHistoryResponse> getSavedJobHistory(
            SavedJobAction action,
            Pageable pageable
    );
}