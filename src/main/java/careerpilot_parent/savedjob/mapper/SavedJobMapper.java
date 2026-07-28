package careerpilot_parent.savedjob.mapper;

import careerpilot_parent.job.entity.JobApplication;
import careerpilot_parent.job.entity.JobPosting;

import careerpilot_parent.savedjob.dto.response.SavedJobHistoryResponse;
import careerpilot_parent.savedjob.dto.response.SavedJobResponse;
import careerpilot_parent.savedjob.dto.response.SavedJobStatusResponse;

import careerpilot_parent.savedjob.entity.SavedJob;
import careerpilot_parent.savedjob.entity.SavedJobHistory;

import org.springframework.stereotype.Component;

@Component
public class SavedJobMapper {

    public SavedJobResponse toResponse(
            SavedJob savedJob,
            boolean applied
    ) {

        if (savedJob == null) {
            return null;
        }

        JobPosting job =
                savedJob.getJobPosting();

        return SavedJobResponse.builder()
                .savedJobId(savedJob.getId())
                .jobId(
                        job == null
                                ? null
                                : job.getId()
                )
                .jobTitle(
                        job == null
                                ? null
                                : job.getTitle()
                )
                .jobSlug(
                        job == null
                                ? null
                                : job.getSlug()
                )
                .companyId(
                        job == null || job.getCompany() == null
                                ? null
                                : job.getCompany().getId()
                )
                .companyName(
                        job == null || job.getCompany() == null
                                ? null
                                : job.getCompany().getName()
                )
                .companyLogoUrl(
                        job == null || job.getCompany() == null
                                ? null
                                : job.getCompany().getLogoUrl()
                )
                .location(
                        job == null
                                ? null
                                : job.getLocation()
                )
                .employmentType(
                        job == null ||
                                job.getEmploymentType() == null
                                ? null
                                : job.getEmploymentType().name()
                )
                .workMode(
                        job == null ||
                                job.getWorkMode() == null
                                ? null
                                : job.getWorkMode().name()
                )
                .experienceLevel(
                        job == null ||
                                job.getExperienceLevel() == null
                                ? null
                                : job.getExperienceLevel().name()
                )
                .minimumExperience(
                        job == null
                                ? null
                                : job.getMinimumExperience()
                )
                .maximumExperience(
                        job == null
                                ? null
                                : job.getMaximumExperience()
                )
                .applicationDeadline(
                        job == null
                                ? null
                                : job.getApplicationDeadline()
                )
                .jobStatus(
                        job == null || job.getStatus() == null
                                ? null
                                : job.getStatus().name()
                )
                .publishedAt(
                        job == null
                                ? null
                                : job.getPublishedAt()
                )
                .savedAt(savedJob.getSavedAt())
                .applied(applied)
                .build();
    }

    public SavedJobStatusResponse toStatusResponse(
            Long jobId,
            SavedJob savedJob
    ) {

        return SavedJobStatusResponse.builder()
                .jobId(jobId)
                .saved(savedJob != null)
                .savedJobId(
                        savedJob == null
                                ? null
                                : savedJob.getId()
                )
                .savedAt(
                        savedJob == null
                                ? null
                                : savedJob.getSavedAt()
                )
                .build();
    }

    public SavedJobHistoryResponse toHistoryResponse(
            SavedJobHistory history
    ) {

        if (history == null) {
            return null;
        }

        return SavedJobHistoryResponse.builder()
                .historyId(history.getId())
                .jobId(
                        history.getJobPosting() == null
                                ? null
                                : history.getJobPosting().getId()
                )
                .jobTitle(
                        history.getJobTitleSnapshot()
                )
                .companyName(
                        history.getCompanyNameSnapshot()
                )
                .location(
                        history.getLocationSnapshot()
                )
                .action(history.getAction())
                .actionAt(history.getActionAt())
                .build();
    }
}