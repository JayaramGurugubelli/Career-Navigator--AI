package careerpilot_parent.admin.service.impl;

import careerpilot_parent.admin.dto.response.AdminDashboardResponse;
import careerpilot_parent.admin.service.AdminDashboardService;
import careerpilot_parent.company.repository.CompanyRepository;
import careerpilot_parent.company.repository.RecruiterProfileRepository;
import careerpilot_parent.interview.enums.InterviewStatus;
import careerpilot_parent.interview.repository.InterviewRepository;
import careerpilot_parent.job.repository.JobApplicationRepository;
import careerpilot_parent.job.repository.JobPostingRepository;
import careerpilot_parent.offer.enums.OfferStatus;
import careerpilot_parent.offer.repository.JobOfferRepository;
import careerpilot_parent.shared.enums.ApplicationStatus;
import careerpilot_parent.shared.enums.RoleName;
import careerpilot_parent.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl
        implements AdminDashboardService {

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    private final RecruiterProfileRepository
            recruiterProfileRepository;

    private final JobPostingRepository
            jobPostingRepository;

    private final JobApplicationRepository
            jobApplicationRepository;

    private final InterviewRepository
            interviewRepository;

    private final JobOfferRepository
            jobOfferRepository;

    @Override
    public AdminDashboardResponse getDashboard() {

        return AdminDashboardResponse.builder()

                .totalUsers(
                        userRepository.count()
                )

                .totalStudents(
                        userRepository.countByRoleName(
                                RoleName.STUDENT
                        )
                )

                .totalRecruiters(
                        userRepository.countByRoleName(
                                RoleName.RECRUITER
                        )
                )

                .totalAdmins(
                        userRepository.countByRoleName(
                                RoleName.ADMIN
                        )
                )

                .totalCompanies(
                        companyRepository.count()
                )

                /*
                 * Company approval is represented by
                 * the verified field, not CompanyStatus.
                 */
                .approvedCompanies(
                        companyRepository.countByVerifiedTrue()
                )

                .pendingCompanies(
                        companyRepository.countByVerifiedFalse()
                )

                .verifiedRecruiters(
                        recruiterProfileRepository
                                .countByVerifiedTrue()
                )

                .pendingRecruiters(
                        recruiterProfileRepository
                                .countByVerifiedFalse()
                )

                .activeRecruiters(
                        recruiterProfileRepository
                                .countByActiveTrue()
                )

                .totalJobs(
                        jobPostingRepository.count()
                )

                /*
                 * Does not depend on a missing OPEN enum.
                 */
                .activeJobs(
                        jobPostingRepository
                                .countByPublishedAtIsNotNullAndClosedAtIsNull()
                )

                .totalApplications(
                        jobApplicationRepository.count()
                )

                .scheduledInterviews(
                        interviewRepository.countByStatus(
                                InterviewStatus.SCHEDULED
                        )
                )

                .offersSent(
                        jobOfferRepository.countByStatus(
                                OfferStatus.SENT
                        )
                )

                .studentsHired(
                        jobApplicationRepository.countByStatus(
                                ApplicationStatus.HIRED
                        )
                )

                .build();
    }
}