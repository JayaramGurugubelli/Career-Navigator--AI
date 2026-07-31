package career_Navigator_parent.admin.service.impl;

import career_Navigator_parent.admin.dto.request.UpdateCompanyStatusRequest;
import career_Navigator_parent.admin.dto.response.AdminCompanyResponse;
import career_Navigator_parent.admin.mapper.AdminMapper;
import career_Navigator_parent.admin.service.AdminCompanyService;
import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.company.entity.Company;
import career_Navigator_parent.company.enums.CompanyStatus;
import career_Navigator_parent.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminCompanyServiceImpl
        implements AdminCompanyService {

    private final CompanyRepository companyRepository;
    private final AdminMapper adminMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<AdminCompanyResponse> getCompanies(
            CompanyStatus status,
            String keyword,
            Pageable pageable
    ) {

        List<Company> filtered =
                companyRepository.findAll()
                        .stream()
                        .filter(company ->
                                status == null ||
                                company.getStatus() == status)
                        .filter(company ->
                                matchesKeyword(company, keyword))
                        .toList();

        int start = Math.min(
                (int) pageable.getOffset(),
                filtered.size()
        );

        int end = Math.min(
                start + pageable.getPageSize(),
                filtered.size()
        );

        List<AdminCompanyResponse> content =
                filtered.subList(start, end)
                        .stream()
                        .map(adminMapper::toCompanyResponse)
                        .toList();

        return new PageImpl<>(
                content,
                pageable,
                filtered.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AdminCompanyResponse getCompanyById(
            Long companyId
    ) {

        return adminMapper.toCompanyResponse(
                getCompany(companyId)
        );
    }

    @Override
    public AdminCompanyResponse updateCompanyStatus(
            Long companyId,
            UpdateCompanyStatusRequest request
    ) {

        Company company = getCompany(companyId);

        if (company.getStatus() ==
                request.getStatus()) {

            throw new IllegalStateException(
                    "Company is already in "
                            + request.getStatus()
                            + " status."
            );
        }

        company.setStatus(request.getStatus());

        return adminMapper.toCompanyResponse(
                companyRepository.save(company)
        );
    }

    private Company getCompany(Long companyId) {

        return companyRepository.findById(companyId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Company not found."
                        )
                );
    }

    private boolean matchesKeyword(
            Company company,
            String keyword
    ) {

        if (keyword == null ||
                keyword.isBlank()) {
            return true;
        }

        String value =
                keyword.trim().toLowerCase();

        return contains(company.getName(), value)
                || contains(company.getIndustry(), value)
                || contains(company.getHeadquarters(), value);
    }

    private boolean contains(
            String source,
            String value
    ) {

        return source != null &&
                source.toLowerCase().contains(value);
    }
}
