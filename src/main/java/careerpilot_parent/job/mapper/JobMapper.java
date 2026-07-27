package careerpilot_parent.job.mapper;

import careerpilot_parent.company.dto.request.CreateCompanyRequest;
import careerpilot_parent.company.dto.request.CreateJobPostingRequest;
import careerpilot_parent.company.dto.request.UpdateJobPostingRequest;

import careerpilot_parent.company.dto.response.CompanyResponse;
import careerpilot_parent.company.dto.response.JobPostingResponse;
import careerpilot_parent.company.dto.response.RecruiterProfileResponse;

import careerpilot_parent.company.entity.Company;
import careerpilot_parent.company.entity.RecruiterProfile;

import careerpilot_parent.company.enums.CompanyStatus;
import careerpilot_parent.company.enums.JobStatus;

import careerpilot_parent.job.dto.response.JobApplicationResponse;
import careerpilot_parent.job.entity.JobApplication;
import careerpilot_parent.job.entity.JobPosting;

import careerpilot_parent.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JobMapper {

    public Company toCompanyEntity(
            CreateCompanyRequest request
    ) {

        if (request == null) {
            return null;
        }

        return Company.builder()
                .name(
                        normalizeRequiredText(
                                request.getName()
                        )
                )
                .description(
                        normalizeOptionalText(
                                request.getDescription()
                        )
                )
                .industry(
                        normalizeOptionalText(
                                request.getIndustry()
                        )
                )
                .companySize(
                        request.getCompanySize()
                )
                .websiteUrl(
                        normalizeOptionalText(
                                request.getWebsiteUrl()
                        )
                )
                .logoUrl(
                        normalizeOptionalText(
                                request.getLogoUrl()
                        )
                )
                .headquarters(
                        normalizeOptionalText(
                                request.getHeadquarters()
                        )
                )
                .foundedYear(
                        request.getFoundedYear()
                )
                .verified(false)
                .status(CompanyStatus.ACTIVE)
                .build();
    }

    public CompanyResponse toCompanyResponse(
            Company company
    ) {

        if (company == null) {
            return null;
        }

        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .slug(company.getSlug())
                .description(company.getDescription())
                .industry(company.getIndustry())
                .companySize(company.getCompanySize())
                .websiteUrl(company.getWebsiteUrl())
                .logoUrl(company.getLogoUrl())
                .headquarters(company.getHeadquarters())
                .foundedYear(company.getFoundedYear())
                .verified(company.isVerified())
                .build();
    }

    public RecruiterProfileResponse toRecruiterResponse(
            RecruiterProfile recruiter
    ) {

        if (recruiter == null) {
            return null;
        }

        return RecruiterProfileResponse.builder()
                .id(recruiter.getId())
                .userId(
                        recruiter.getUser() == null
                                ? null
                                : recruiter.getUser().getId()
                )
                .recruiterName(
                        buildRecruiterName(
                                recruiter.getUser()
                        )
                )
                .companyId(
                        recruiter.getCompany() == null
                                ? null
                                : recruiter.getCompany().getId()
                )
                .companyName(
                        recruiter.getCompany() == null
                                ? null
                                : recruiter.getCompany().getName()
                )
                .designation(recruiter.getDesignation())
                .officialEmail(recruiter.getOfficialEmail())
                .phoneNumber(recruiter.getPhoneNumber())
                .linkedinUrl(recruiter.getLinkedinUrl())
                .verified(recruiter.isVerified())
                .active(recruiter.isActive())
                .build();
    }

    public JobPosting toJobEntity(
            CreateJobPostingRequest request
    ) {

        if (request == null) {
            return null;
        }

        return JobPosting.builder()
                .title(
                        normalizeRequiredText(
                                request.getTitle()
                        )
                )
                .description(
                        normalizeRequiredText(
                                request.getDescription()
                        )
                )
                .responsibilities(
                        normalizeOptionalText(
                                request.getResponsibilities()
                        )
                )
                .qualifications(
                        normalizeOptionalText(
                                request.getQualifications()
                        )
                )
                .preferredQualifications(
                        normalizeOptionalText(
                                request.getPreferredQualifications()
                        )
                )
                .employmentType(
                        request.getEmploymentType()
                )
                .workMode(
                        request.getWorkMode()
                )
                .experienceLevel(
                        request.getExperienceLevel()
                )
                .location(
                        normalizeOptionalText(
                                request.getLocation()
                        )
                )
                .minimumExperience(
                        request.getMinimumExperience()
                )
                .maximumExperience(
                        request.getMaximumExperience()
                )
                .minimumSalary(
                        request.getMinimumSalary()
                )
                .maximumSalary(
                        request.getMaximumSalary()
                )
                .currency(
                        request.getCurrency()
                )
                .salaryDisclosed(
                        request.isSalaryDisclosed()
                )
                .requiredSkills(
                        normalizeSkills(
                                request.getRequiredSkills()
                        )
                )
                .numberOfOpenings(
                        request.getNumberOfOpenings()
                )
                .applicationDeadline(
                        request.getApplicationDeadline()
                )
                .status(JobStatus.DRAFT)
                .viewCount(0L)
                .applicationCount(0L)
                .build();
    }

    public void updateJobEntity(
            UpdateJobPostingRequest request,
            JobPosting job
    ) {

        if (request == null || job == null) {
            return;
        }

        job.setTitle(
                normalizeRequiredText(
                        request.getTitle()
                )
        );

        job.setDescription(
                normalizeRequiredText(
                        request.getDescription()
                )
        );

        job.setResponsibilities(
                normalizeOptionalText(
                        request.getResponsibilities()
                )
        );

        job.setQualifications(
                normalizeOptionalText(
                        request.getQualifications()
                )
        );

        job.setPreferredQualifications(
                normalizeOptionalText(
                        request.getPreferredQualifications()
                )
        );

        job.setEmploymentType(
                request.getEmploymentType()
        );

        job.setWorkMode(
                request.getWorkMode()
        );

        job.setExperienceLevel(
                request.getExperienceLevel()
        );

        job.setLocation(
                normalizeOptionalText(
                        request.getLocation()
                )
        );

        job.setMinimumExperience(
                request.getMinimumExperience()
        );

        job.setMaximumExperience(
                request.getMaximumExperience()
        );

        job.setMinimumSalary(
                request.getMinimumSalary()
        );

        job.setMaximumSalary(
                request.getMaximumSalary()
        );

        job.setCurrency(
                request.getCurrency()
        );

        job.setSalaryDisclosed(
                request.isSalaryDisclosed()
        );

        job.setRequiredSkills(
                normalizeSkills(
                        request.getRequiredSkills()
                )
        );

        job.setNumberOfOpenings(
                request.getNumberOfOpenings()
        );

        job.setApplicationDeadline(
                request.getApplicationDeadline()
        );
    }

    public JobPostingResponse toJobResponse(
            JobPosting job
    ) {

        if (job == null) {
            return null;
        }

        return JobPostingResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .slug(job.getSlug())
                .description(job.getDescription())
                .responsibilities(
                        job.getResponsibilities()
                )
                .qualifications(
                        job.getQualifications()
                )
                .preferredQualifications(
                        job.getPreferredQualifications()
                )
                .employmentType(
                        job.getEmploymentType()
                )
                .workMode(
                        job.getWorkMode()
                )
                .experienceLevel(
                        job.getExperienceLevel()
                )
                .location(job.getLocation())
                .minimumExperience(
                        job.getMinimumExperience()
                )
                .maximumExperience(
                        job.getMaximumExperience()
                )
                .minimumSalary(
                        job.isSalaryDisclosed()
                                ? job.getMinimumSalary()
                                : null
                )
                .maximumSalary(
                        job.isSalaryDisclosed()
                                ? job.getMaximumSalary()
                                : null
                )
                .currency(
                        job.isSalaryDisclosed()
                                ? job.getCurrency()
                                : null
                )
                .salaryDisclosed(
                        job.isSalaryDisclosed()
                )
                .requiredSkills(
                        job.getRequiredSkills() == null
                                ? new HashSet<>()
                                : new HashSet<>(
                                job.getRequiredSkills()
                        )
                )
                .numberOfOpenings(
                        job.getNumberOfOpenings()
                )
                .applicationDeadline(
                        job.getApplicationDeadline()
                )
                .status(job.getStatus())
                .publishedAt(job.getPublishedAt())
                .closedAt(job.getClosedAt())
                .viewCount(job.getViewCount())
                .applicationCount(
                        job.getApplicationCount()
                )
                .company(
                        toCompanyResponse(
                                job.getCompany()
                        )
                )
                .recruiterId(
                        job.getRecruiter() == null
                                ? null
                                : job.getRecruiter().getId()
                )
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    public JobApplicationResponse toResponse(
            JobApplication application
    ) {

        if (application == null) {
            return null;
        }

        JobPosting job =
                application.getJobPosting();

        Company company =
                job == null
                        ? null
                        : job.getCompany();

        return JobApplicationResponse.builder()
                .id(application.getId())
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
                        company == null
                                ? null
                                : company.getId()
                )
                .companyName(
                        company == null
                                ? null
                                : company.getName()
                )
                .companyLogoUrl(
                        company == null
                                ? null
                                : company.getLogoUrl()
                )
                .studentId(
                        application.getStudent() == null
                                ? null
                                : application
                                .getStudent()
                                .getId()
                )
                .resumeId(
                        application.getResume() == null
                                ? null
                                : application
                                .getResume()
                                .getId()
                )
                .resumeTitle(
                        application.getResume() == null
                                ? null
                                : application
                                .getResume()
                                .getResumeTitle()
                )
                .coverLetter(
                        application.getCoverLetter()
                )
                .status(application.getStatus())
                .recruiterNotes(
                        application.getRecruiterNotes()
                )
                .appliedAt(application.getAppliedAt())
                .lastStatusChangedAt(
                        application
                                .getLastStatusChangedAt()
                )
                .withdrawnAt(
                        application.getWithdrawnAt()
                )
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    private String normalizeRequiredText(
            String value
    ) {

        return value == null
                ? null
                : value.trim();
    }

    private String normalizeOptionalText(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private Set<String> normalizeSkills(
            Set<String> skills
    ) {

        if (skills == null) {
            return new HashSet<>();
        }

        return skills.stream()
                .filter(skill ->
                        skill != null &&
                                !skill.isBlank()
                )
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    private String buildRecruiterName(
            User user
    ) {

        if (user == null) {
            return null;
        }

        String firstName =
                normalizeOptionalText(
                        user.getFirstName()
                );

        String lastName =
                normalizeOptionalText(
                        user.getLastName()
                );

        if (firstName == null) {
            return lastName;
        }

        if (lastName == null) {
            return firstName;
        }

        return firstName + " " + lastName;
    }
}