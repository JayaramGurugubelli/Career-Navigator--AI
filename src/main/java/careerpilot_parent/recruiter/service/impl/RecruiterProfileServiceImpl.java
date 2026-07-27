package careerpilot_parent.recruiter.service.impl;

import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.company.dto.request.CreateRecruiterProfileRequest;
import careerpilot_parent.company.dto.request.UpdateRecruiterProfileRequest;
import careerpilot_parent.company.dto.response.RecruiterProfileResponse;
import careerpilot_parent.company.entity.Company;
import careerpilot_parent.company.entity.RecruiterProfile;
import careerpilot_parent.company.repository.CompanyRepository;
import careerpilot_parent.company.repository.RecruiterProfileRepository;
import careerpilot_parent.job.mapper.JobMapper;
import careerpilot_parent.job.util.SlugGenerator;
import careerpilot_parent.recruiter.service.RecruiterProfileService;
import careerpilot_parent.security.util.SecurityUtils;
import careerpilot_parent.user.entity.User;
import careerpilot_parent.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class RecruiterProfileServiceImpl
        implements RecruiterProfileService {

    private final RecruiterProfileRepository recruiterProfileRepository;

    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;

    private final SecurityUtils securityUtils;

    private final JobMapper jobMapper;

    private final SlugGenerator slugGenerator;

    @Override
    public RecruiterProfileResponse createProfile(
            CreateRecruiterProfileRequest request
    ) {

        Long userId = securityUtils.getCurrentUserId();

        if (recruiterProfileRepository.existsByUserId(userId)) {
            throw new IllegalStateException(
                    "Recruiter profile already exists."
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found."
                        )
                );

        String officialEmail =
                normalizeEmail(request.getOfficialEmail());

        if (recruiterProfileRepository
                .existsByOfficialEmailIgnoreCase(officialEmail)) {

            throw new IllegalStateException(
                    "A recruiter profile already exists with this official email."
            );
        }

        Company company = resolveCompany(request);

        RecruiterProfile recruiterProfile =
                RecruiterProfile.builder()
                        .user(user)
                        .company(company)
                        .designation(
                                request.getDesignation().trim()
                        )
                        .officialEmail(officialEmail)
                        .phoneNumber(
                                normalizeNullable(
                                        request.getPhoneNumber()
                                )
                        )
                        .linkedinUrl(
                                normalizeNullable(
                                        request.getLinkedinUrl()
                                )
                        )
                        .verified(false)
                        .active(true)
                        .build();

        RecruiterProfile savedProfile =
                recruiterProfileRepository.save(
                        recruiterProfile
                );

        return jobMapper.toRecruiterResponse(
                savedProfile
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RecruiterProfileResponse getMyProfile() {

        return jobMapper.toRecruiterResponse(
                getCurrentRecruiterProfile()
        );
    }

    @Override
    public RecruiterProfileResponse updateProfile(
            UpdateRecruiterProfileRequest request
    ) {

        RecruiterProfile recruiterProfile =
                getCurrentRecruiterProfile();

        Company company = companyRepository
                .findById(request.getCompanyId())
                .filter(Company::isActive)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Active company not found."
                        )
                );

        String officialEmail =
                normalizeEmail(
                        request.getOfficialEmail()
                );

        boolean emailChanged =
                !officialEmail.equalsIgnoreCase(
                        recruiterProfile.getOfficialEmail()
                );

        if (emailChanged &&
                recruiterProfileRepository
                        .existsByOfficialEmailIgnoreCase(
                                officialEmail
                        )) {

            throw new IllegalStateException(
                    "A recruiter profile already exists with this official email."
            );
        }

        recruiterProfile.setCompany(company);

        recruiterProfile.setDesignation(
                request.getDesignation().trim()
        );

        recruiterProfile.setOfficialEmail(
                officialEmail
        );

        recruiterProfile.setPhoneNumber(
                normalizeNullable(
                        request.getPhoneNumber()
                )
        );

        recruiterProfile.setLinkedinUrl(
                normalizeNullable(
                        request.getLinkedinUrl()
                )
        );

        RecruiterProfile savedProfile =
                recruiterProfileRepository.save(
                        recruiterProfile
                );

        return jobMapper.toRecruiterResponse(
                savedProfile
        );
    }

    @Override
    public void deactivateProfile() {

        RecruiterProfile recruiterProfile =
                getCurrentRecruiterProfile();

        recruiterProfile.setActive(false);

        recruiterProfileRepository.save(
                recruiterProfile
        );
    }

    private Company resolveCompany(
            CreateRecruiterProfileRequest request
    ) {

        boolean existingCompanyProvided =
                request.getExistingCompanyId() != null;

        boolean newCompanyProvided =
                request.getCompany() != null;

        if (existingCompanyProvided && newCompanyProvided) {
            throw new IllegalArgumentException(
                    "Provide either an existing company ID or new company details, not both."
            );
        }

        if (!existingCompanyProvided && !newCompanyProvided) {
            throw new IllegalArgumentException(
                    "Provide an existing company ID or new company details."
            );
        }

        if (existingCompanyProvided) {

            return companyRepository
                    .findById(
                            request.getExistingCompanyId()
                    )
                    .filter(Company::isActive)
                    .orElseThrow(
                            () -> new ResourceNotFoundException(
                                    "Active company not found."
                            )
                    );
        }

        String companyName =
                request.getCompany()
                        .getName()
                        .trim();

        if (companyRepository
                .existsByNameIgnoreCase(companyName)) {

            throw new IllegalStateException(
                    "A company with this name already exists. Use its existing company ID."
            );
        }

        Company company =
                jobMapper.toCompanyEntity(
                        request.getCompany()
                );

        company.setSlug(
                generateUniqueCompanySlug(
                        company.getName()
                )
        );

        return companyRepository.save(company);
    }

    private String generateUniqueCompanySlug(
            String companyName
    ) {

        String baseSlug =
                slugGenerator.generate(companyName);

        String slug = baseSlug;

        int suffix = 1;

        while (companyRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + suffix;
            suffix++;
        }

        return slug;
    }

    private RecruiterProfile getCurrentRecruiterProfile() {

        Long userId =
                securityUtils.getCurrentUserId();

        return recruiterProfileRepository
                .findByUserIdAndActiveTrue(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Active recruiter profile not found."
                        )
                );
    }

    private String normalizeEmail(
            String email
    ) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Official email is required."
            );
        }

        return email.trim()
                .toLowerCase(Locale.ROOT);
    }

    private String normalizeNullable(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}