package career_Navigator_parent.job.service;

import career_Navigator_parent.company.dto.response.JobPostingResponse;
import career_Navigator_parent.company.enums.EmploymentType;
import career_Navigator_parent.company.enums.ExperienceLevel;
import career_Navigator_parent.company.enums.WorkMode;
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