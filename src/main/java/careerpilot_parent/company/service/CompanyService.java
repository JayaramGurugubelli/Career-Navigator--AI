package careerpilot_parent.company.service;

import careerpilot_parent.company.dto.request.CreateCompanyRequest;
import careerpilot_parent.company.dto.request.UpdateCompanyRequest;
import careerpilot_parent.company.dto.response.CompanyResponse;

public interface CompanyService {

    CompanyResponse createCompany(
            CreateCompanyRequest request
    );

    CompanyResponse getCurrentRecruiterCompany();

    CompanyResponse getCompanyById(
            Long companyId
    );

    CompanyResponse updateCompany(
            Long companyId,
            UpdateCompanyRequest request
    );

    void deleteCompany(
            Long companyId
    );
}