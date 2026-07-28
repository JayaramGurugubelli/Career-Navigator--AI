package careerpilot_parent.admin.mapper;

import careerpilot_parent.admin.dto.response.AdminCompanyResponse;
import careerpilot_parent.admin.dto.response.AdminJobResponse;
import careerpilot_parent.admin.dto.response.AdminRecruiterResponse;
import careerpilot_parent.admin.dto.response.AdminUserResponse;
import careerpilot_parent.company.entity.Company;
import careerpilot_parent.company.entity.RecruiterProfile;
import careerpilot_parent.job.entity.JobPosting;
import careerpilot_parent.shared.enums.RoleName;
import careerpilot_parent.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AdminMapper {

    public AdminUserResponse toUserResponse(User user) {

        if (user == null) {
            return null;
        }

        Set<RoleName> roles = user.getRoles() == null
                ? Collections.emptySet()
                : user.getRoles()
                .stream()
                .filter(userRole ->
                        userRole != null &&
                                userRole.getRole() != null &&
                                userRole.getRole().getName() != null
                )
                .map(userRole ->
                        userRole.getRole().getName()
                )
                .collect(Collectors.toSet());

        return AdminUserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .accountStatus(user.getAccountStatus())
                .emailVerified(
                        Boolean.TRUE.equals(
                                user.getEmailVerified()
                        )
                )
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public AdminRecruiterResponse toRecruiterResponse(
            RecruiterProfile recruiter
    ) {

        if (recruiter == null) {
            return null;
        }

        User user = recruiter.getUser();
        Company company = recruiter.getCompany();

        return AdminRecruiterResponse.builder()
                .id(recruiter.getId())

                .userId(
                        user != null
                                ? user.getId()
                                : null
                )

                /*
                 * RecruiterProfile does not contain recruiterName.
                 * Therefore, it is generated from the related User.
                 */
                .recruiterName(
                        buildFullName(user)
                )

                .userEmail(
                        user != null
                                ? user.getEmail()
                                : null
                )

                .companyId(
                        company != null
                                ? company.getId()
                                : null
                )

                .companyName(
                        company != null
                                ? company.getName()
                                : null
                )

                .designation(
                        recruiter.getDesignation()
                )

                .officialEmail(
                        recruiter.getOfficialEmail()
                )

                .phoneNumber(
                        recruiter.getPhoneNumber()
                )

                .linkedinUrl(
                        recruiter.getLinkedinUrl()
                )

                .verified(
                        recruiter.isVerified()
                )

                .active(
                        recruiter.isActive()
                )

                .build();
    }

    public AdminCompanyResponse toCompanyResponse(
            Company company
    ) {

        if (company == null) {
            return null;
        }

        return AdminCompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())

                /*
                 * Entity field is websiteUrl,
                 * so Lombok generates getWebsiteUrl().
                 */
                .website(company.getWebsiteUrl())

                .industry(company.getIndustry())
                .headquarters(company.getHeadquarters())
                .companySize(company.getCompanySize())
                .status(company.getStatus())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }

    public AdminJobResponse toJobResponse(
            JobPosting job
    ) {

        if (job == null) {
            return null;
        }

        Company company = job.getCompany();
        RecruiterProfile recruiter = job.getRecruiter();

        return AdminJobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())

                .companyName(
                        company != null
                                ? company.getName()
                                : null
                )

                .recruiterId(
                        recruiter != null
                                ? recruiter.getId()
                                : null
                )

                /*
                 * RecruiterProfile does not contain recruiterName.
                 * Name is taken from recruiter.user.
                 */
                .recruiterName(
                        recruiter != null
                                ? buildFullName(
                                recruiter.getUser()
                        )
                                : null
                )

                .status(job.getStatus())
                .location(job.getLocation())
                .publishedAt(job.getPublishedAt())
                .applicationDeadline(
                        job.getApplicationDeadline()
                )
                .build();
    }

    private String buildFullName(User user) {

        if (user == null) {
            return null;
        }

        String firstName = user.getFirstName() == null
                ? ""
                : user.getFirstName().trim();

        String lastName = user.getLastName() == null
                ? ""
                : user.getLastName().trim();

        String fullName =
                (firstName + " " + lastName).trim();

        return fullName.isBlank()
                ? user.getUsername()
                : fullName;
    }
}