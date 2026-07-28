package careerpilot_parent.admin.service;

import careerpilot_parent.admin.dto.request.UpdateCompanyStatusRequest;
import careerpilot_parent.admin.dto.response.AdminCompanyResponse;
import careerpilot_parent.company.enums.CompanyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminCompanyService {

    Page<AdminCompanyResponse> getCompanies(
            CompanyStatus status,
            String keyword,
            Pageable pageable
    );

    AdminCompanyResponse getCompanyById(Long companyId);

    AdminCompanyResponse updateCompanyStatus(
            Long companyId,
            UpdateCompanyStatusRequest request
    );
}
