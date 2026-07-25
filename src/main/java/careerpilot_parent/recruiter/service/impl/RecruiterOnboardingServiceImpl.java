package careerpilot_parent.recruiter.service.impl;

import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.company.dto.request.CreateRecruiterProfileRequest;
import careerpilot_parent.company.dto.response.RecruiterProfileResponse;
import careerpilot_parent.company.entity.Company;
import careerpilot_parent.company.entity.RecruiterProfile;
import careerpilot_parent.job.mapper.JobMapper;
import careerpilot_parent.company.repository.CompanyRepository;
import careerpilot_parent.company.repository.RecruiterProfileRepository;
import careerpilot_parent.recruiter.service.RecruiterOnboardingService;
import careerpilot_parent.job.util.SlugGenerator;
import careerpilot_parent.security.util.SecurityUtils;
import careerpilot_parent.user.entity.User;
import careerpilot_parent.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RecruiterOnboardingServiceImpl
        implements RecruiterOnboardingService {

    private final RecruiterProfileRepository recruiterRepository;

    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;

    private final SecurityUtils securityUtils;

    private final JobMapper jobMapper;

    private final SlugGenerator slugGenerator;

    @Override
    public RecruiterProfileResponse createProfile(
            CreateRecruiterProfileRequest request
    ) {

        Long userId =
                securityUtils.getCurrentUserId();

        if (recruiterRepository.existsByUserId(userId)) {
            throw new IllegalStateException(
                    "Recruiter profile already exists."
            );
        }

        User user =
                userRepository.findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "User not found."
                                )
                        );

        Company company =
                resolveCompany(request);

        RecruiterProfile recruiter =
                RecruiterProfile.builder()
                        .user(user)
                        .company(company)
                        .designation(
                                request.getDesignation().trim()
                        )
                        .officialEmail(
                                request.getOfficialEmail()
                                        .trim()
                                        .toLowerCase()
                        )
                        .phoneNumber(request.getPhoneNumber())
                        .linkedinUrl(request.getLinkedinUrl())
                        .verified(false)
                        .active(true)
                        .build();

        RecruiterProfile savedRecruiter =
                recruiterRepository.save(recruiter);

        return jobMapper.toRecruiterResponse(
                savedRecruiter
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RecruiterProfileResponse getMyProfile() {

        return jobMapper.toRecruiterResponse(
                getCurrentRecruiter()
        );
    }

    private Company resolveCompany(
            CreateRecruiterProfileRequest request
    ) {

        if (request.getExistingCompanyId() != null) {

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

        if (request.getCompany() == null) {
            throw new IllegalArgumentException(
                    "Provide an existing company id or company details."
            );
        }

        if (companyRepository.existsByNameIgnoreCase(
                request.getCompany().getName().trim()
        )) {
            throw new IllegalStateException(
                    "A company with this name already exists. Use its existing company id."
            );
        }

        Company company =
                jobMapper.toCompanyEntity(
                        request.getCompany()
                );

        company.setSlug(
                slugGenerator.generate(
                        company.getName()
                )
        );

        return companyRepository.save(company);
    }

    private RecruiterProfile getCurrentRecruiter() {

        Long userId =
                securityUtils.getCurrentUserId();

        return recruiterRepository
                .findByUserId(userId)
                .filter(RecruiterProfile::isActive)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Active recruiter profile not found."
                        )
                );
    }
}