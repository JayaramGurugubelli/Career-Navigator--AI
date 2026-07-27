package careerpilot_parent.job.service;

import careerpilot_parent.company.dto.response.JobPostingResponse;
import careerpilot_parent.company.enums.EmploymentType;
import careerpilot_parent.company.enums.ExperienceLevel;
import careerpilot_parent.company.enums.WorkMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PublicJobService {

    Page<JobPostingResponse> searchJobs(
            String keyword,
            String location,
            EmploymentType employmentType,
            WorkMode workMode,
            ExperienceLevel experienceLevel,
            Pageable pageable
    );

    JobPostingResponse getJobBySlug(
            String slug
    );
}