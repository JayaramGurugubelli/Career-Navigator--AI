package career_Navigator_parent.savedjob.service;

import career_Navigator_parent.savedjob.dto.response.SavedJobHistoryResponse;
import career_Navigator_parent.savedjob.dto.response.SavedJobResponse;
import career_Navigator_parent.savedjob.dto.response.SavedJobStatusResponse;

import career_Navigator_parent.savedjob.enums.SavedJobAction;

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