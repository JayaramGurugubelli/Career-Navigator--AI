package careerpilot_parent.job.service.impl;

import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.company.dto.response.JobPostingResponse;
import careerpilot_parent.company.enums.EmploymentType;
import careerpilot_parent.company.enums.ExperienceLevel;
import careerpilot_parent.company.enums.JobStatus;
import careerpilot_parent.company.enums.WorkMode;
import careerpilot_parent.job.entity.JobPosting;
import careerpilot_parent.job.mapper.JobMapper;
import careerpilot_parent.job.repository.JobPostingRepository;
import careerpilot_parent.job.service.PublicJobService;
import careerpilot_parent.job.specification.JobPostingSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class PublicJobServiceImpl
        implements PublicJobService {

    private final JobPostingRepository jobRepository;
    private final JobMapper jobMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<JobPostingResponse> searchJobs(
            String keyword,
            String location,
            EmploymentType employmentType,
            WorkMode workMode,
            ExperienceLevel experienceLevel,
            Pageable pageable
    ) {

        Specification<JobPosting> specification =
                Specification
                        .where(
                                JobPostingSpecification
                                        .isPubliclyAvailable()
                        )
                        .and(
                                JobPostingSpecification
                                        .hasKeyword(keyword)
                        )
                        .and(
                                JobPostingSpecification
                                        .hasLocation(location)
                        )
                        .and(
                                JobPostingSpecification
                                        .hasEmploymentType(
                                                employmentType
                                        )
                        )
                        .and(
                                JobPostingSpecification
                                        .hasWorkMode(workMode)
                        )
                        .and(
                                JobPostingSpecification
                                        .hasExperienceLevel(
                                                experienceLevel
                                        )
                        );

        return jobRepository
                .findAll(
                        specification,
                        pageable
                )
                .map(jobMapper::toJobResponse);
    }

    @Override
    public JobPostingResponse getJobBySlug(
            String slug
    ) {

        JobPosting job =
                jobRepository
                        .findBySlugAndStatus(
                                slug,
                                JobStatus.PUBLISHED
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Published job not found."
                                )
                        );

        if (job.getCompany() == null
                || !job.getCompany().isActive()) {

            throw new ResourceNotFoundException(
                    "Published job not found."
            );
        }

        if (job.getRecruiter() == null
                || !job.getRecruiter().isActive()) {

            throw new ResourceNotFoundException(
                    "Published job not found."
            );
        }

        if (job.getApplicationDeadline() != null
                && job.getApplicationDeadline()
                .isBefore(LocalDate.now())) {

            job.setStatus(JobStatus.EXPIRED);
            jobRepository.save(job);

            throw new ResourceNotFoundException(
                    "This job has expired."
            );
        }

        Long currentViewCount =
                job.getViewCount() == null
                        ? 0L
                        : job.getViewCount();

        job.setViewCount(
                currentViewCount + 1
        );

        JobPosting updated =
                jobRepository.save(job);

        return jobMapper.toJobResponse(updated);
    }
}