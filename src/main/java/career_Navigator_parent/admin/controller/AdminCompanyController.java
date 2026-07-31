package career_Navigator_parent.admin.controller;

import career_Navigator_parent.admin.dto.request.UpdateCompanyStatusRequest;
import career_Navigator_parent.admin.dto.response.AdminCompanyResponse;
import career_Navigator_parent.admin.service.AdminCompanyService;
import career_Navigator_parent.company.enums.CompanyStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/companies")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCompanyController {

    private final AdminCompanyService
            adminCompanyService;

    @GetMapping
    public ResponseEntity<Page<AdminCompanyResponse>>
    getCompanies(
            @RequestParam(required = false)
            CompanyStatus status,

            @RequestParam(required = false)
            String keyword,

            @PageableDefault(size = 20)
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                adminCompanyService.getCompanies(
                        status,
                        keyword,
                        pageable
                )
        );
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<AdminCompanyResponse>
    getCompanyById(
            @PathVariable Long companyId
    ) {

        return ResponseEntity.ok(
                adminCompanyService
                        .getCompanyById(companyId)
        );
    }

    @PatchMapping("/{companyId}/status")
    public ResponseEntity<AdminCompanyResponse>
    updateCompanyStatus(
            @PathVariable Long companyId,
            @Valid
            @RequestBody
            UpdateCompanyStatusRequest request
    ) {

        return ResponseEntity.ok(
                adminCompanyService
                        .updateCompanyStatus(
                                companyId,
                                request
                        )
        );
    }
}
