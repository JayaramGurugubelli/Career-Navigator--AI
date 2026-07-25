package careerpilot_parent.company.service.impl;

import careerpilot_parent.company.dto.request.CreateCompanyRequest;
import careerpilot_parent.company.dto.request.UpdateCompanyRequest;
import careerpilot_parent.company.dto.response.CompanyResponse;

import careerpilot_parent.company.entity.Company;

import careerpilot_parent.company.mapper.CompanyMapper;

import careerpilot_parent.company.repository.CompanyRepository;

import careerpilot_parent.company.service.CompanyService;

import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.common.exception.UserNotFoundException;

import careerpilot_parent.security.util.SecurityUtils;

import careerpilot_parent.user.entity.User;
import careerpilot_parent.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl
        implements CompanyService {

    private static final Pattern
            NON_LATIN_PATTERN =
            Pattern.compile("[^\\w-]");

    private static final Pattern
            WHITESPACE_PATTERN =
            Pattern.compile("[\\s]+");

    private final CompanyRepository
            companyRepository;

    private final UserRepository
            userRepository;

    private final CompanyMapper
            companyMapper;

    private final SecurityUtils
            securityUtils;

    @Override
    public CompanyResponse createCompany(
            CreateCompanyRequest request
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        User recruiter =
                getCurrentUser(currentUserId);

        if (companyRepository
                .existsByOwnerId(currentUserId)) {

            throw new IllegalStateException(
                    "You have already created a company."
            );
        }

        String slug =
                generateUniqueSlug(
                        request.getName()
                );

        Company company =
                companyMapper.toEntity(
                        request,
                        recruiter,
                        slug
                );

        try {

            Company savedCompany =
                    companyRepository.save(company);

            return companyMapper.toResponse(
                    savedCompany
            );

        } catch (
                DataIntegrityViolationException exception
        ) {

            throw new IllegalStateException(
                    "A company with the same information already exists.",
                    exception
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse
    getCurrentRecruiterCompany() {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        Company company =
                companyRepository
                        .findByOwnerIdWithOwner(
                                currentUserId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Company not found for the current recruiter."
                                )
                        );

        return companyMapper.toResponse(company);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(
            Long companyId
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        Company company =
                companyRepository
                        .findByIdAndOwnerId(
                                companyId,
                                currentUserId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Company not found or you do not own this company."
                                )
                        );

        return companyMapper.toResponse(company);
    }

    @Override
    public CompanyResponse updateCompany(
            Long companyId,
            UpdateCompanyRequest request
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        Company company =
                companyRepository
                        .findByIdAndOwnerId(
                                companyId,
                                currentUserId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Company not found or you do not own this company."
                                )
                        );

        String oldCompanyName =
                company.getName();

        companyMapper.updateEntity(
                request,
                company
        );

        /*
         * Regenerate slug only when the company name
         * has changed.
         */
        if (request.getName() != null
                && !company.getName()
                .equalsIgnoreCase(
                        oldCompanyName
                )) {

            company.setSlug(
                    generateUniqueSlug(
                            company.getName()
                    )
            );
        }

        Company updatedCompany =
                companyRepository.save(company);

        return companyMapper.toResponse(
                updatedCompany
        );
    }

    @Override
    public void deleteCompany(
            Long companyId
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        Company company =
                companyRepository
                        .findByIdAndOwnerId(
                                companyId,
                                currentUserId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Company not found or you do not own this company."
                                )
                        );

        companyRepository.delete(company);
    }

    private User getCurrentUser(
            Long userId
    ) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Authenticated user was not found."
                        )
                );
    }

    private String generateUniqueSlug(
            String companyName
    ) {

        String baseSlug =
                slugify(companyName);

        String candidateSlug =
                baseSlug;

        int counter = 1;

        while (companyRepository
                .existsBySlug(candidateSlug)) {

            candidateSlug =
                    baseSlug
                            + "-"
                            + counter;

            counter++;
        }

        /*
         * Prevent an unexpectedly large loop during
         * concurrent requests.
         */
        if (counter > 1000) {

            candidateSlug =
                    baseSlug
                            + "-"
                            + UUID.randomUUID()
                            .toString()
                            .substring(0, 8);
        }

        return candidateSlug;
    }

    private String slugify(
            String value
    ) {

        if (value == null || value.isBlank()) {

            return "company-"
                    + UUID.randomUUID()
                    .toString()
                    .substring(0, 8);
        }

        String normalized =
                Normalizer.normalize(
                        value,
                        Normalizer.Form.NFD
                );

        String slug =
                normalized
                        .replaceAll(
                                "\\p{M}",
                                ""
                        )
                        .toLowerCase(
                                Locale.ENGLISH
                        )
                        .trim();

        slug =
                WHITESPACE_PATTERN
                        .matcher(slug)
                        .replaceAll("-");

        slug =
                NON_LATIN_PATTERN
                        .matcher(slug)
                        .replaceAll("");

        slug =
                slug.replaceAll(
                        "-{2,}",
                        "-"
                );

        slug =
                slug.replaceAll(
                        "^-|-$",
                        ""
                );

        if (slug.isBlank()) {

            return "company-"
                    + UUID.randomUUID()
                    .toString()
                    .substring(0, 8);
        }

        return slug;
    }
}