package careerpilot_parent.company.mapper;

import careerpilot_parent.company.dto.request.CreateCompanyRequest;
import careerpilot_parent.company.dto.request.UpdateCompanyRequest;
import careerpilot_parent.company.dto.response.CompanyResponse;
import careerpilot_parent.company.entity.Company;
import careerpilot_parent.company.enums.CompanyStatus;
import careerpilot_parent.user.entity.User;

import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public Company toEntity(
            CreateCompanyRequest request,
            User owner,
            String slug
    ) {

        if (request == null) {
            return null;
        }

        return Company.builder()
                .name(
                        normalizeRequired(
                                request.getName()
                        )
                )
                .slug(slug)
                .description(
                        normalizeNullable(
                                request.getDescription()
                        )
                )
                .industry(
                        normalizeRequired(
                                request.getIndustry()
                        )
                )
                .companySize(
                        request.getCompanySize()
                )
                .websiteUrl(
                        normalizeNullable(
                                request.getWebsiteUrl()
                        )
                )
                .logoUrl(
                        normalizeNullable(
                                request.getLogoUrl()
                        )
                )
                .headquarters(
                        normalizeNullable(
                                request.getHeadquarters()
                        )
                )
                .foundedYear(
                        request.getFoundedYear()
                )
                .contactEmail(
                        normalizeEmail(
                                request.getContactEmail()
                        )
                )
                .contactPhone(
                        normalizeNullable(
                                request.getContactPhone()
                        )
                )
                .status(
                        CompanyStatus.ACTIVE
                )
                .verified(false)
                .owner(owner)
                .build();
    }

    public void updateEntity(
            UpdateCompanyRequest request,
            Company company
    ) {

        if (request == null || company == null) {
            return;
        }

        if (request.getName() != null) {
            company.setName(
                    normalizeRequired(
                            request.getName()
                    )
            );
        }

        if (request.getDescription() != null) {
            company.setDescription(
                    normalizeNullable(
                            request.getDescription()
                    )
            );
        }

        if (request.getIndustry() != null) {
            company.setIndustry(
                    normalizeRequired(
                            request.getIndustry()
                    )
            );
        }

        if (request.getCompanySize() != null) {
            company.setCompanySize(
                    request.getCompanySize()
            );
        }

        if (request.getWebsiteUrl() != null) {
            company.setWebsiteUrl(
                    normalizeNullable(
                            request.getWebsiteUrl()
                    )
            );
        }

        if (request.getLogoUrl() != null) {
            company.setLogoUrl(
                    normalizeNullable(
                            request.getLogoUrl()
                    )
            );
        }

        if (request.getHeadquarters() != null) {
            company.setHeadquarters(
                    normalizeNullable(
                            request.getHeadquarters()
                    )
            );
        }

        if (request.getFoundedYear() != null) {
            company.setFoundedYear(
                    request.getFoundedYear()
            );
        }

        if (request.getContactEmail() != null) {
            company.setContactEmail(
                    normalizeEmail(
                            request.getContactEmail()
                    )
            );
        }

        if (request.getContactPhone() != null) {
            company.setContactPhone(
                    normalizeNullable(
                            request.getContactPhone()
                    )
            );
        }
    }

    public CompanyResponse toResponse(
            Company company
    ) {

        if (company == null) {
            return null;
        }

        User owner =
                company.getOwner();

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
                .contactEmail(company.getContactEmail())
                .contactPhone(company.getContactPhone())
                .status(company.getStatus())
                .verified(company.getVerified())
                .ownerId(
                        owner != null
                                ? owner.getId()
                                : null
                )
                .ownerUsername(
                        owner != null
                                ? owner.getUsername()
                                : null
                )
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }

    private String normalizeRequired(
            String value
    ) {

        return value == null
                ? null
                : value.trim();
    }

    private String normalizeNullable(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String normalizeEmail(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value
                .trim()
                .toLowerCase();
    }
}