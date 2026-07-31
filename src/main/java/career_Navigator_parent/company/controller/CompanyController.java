package career_Navigator_parent.company.controller;

import career_Navigator_parent.company.dto.request.CreateCompanyRequest;
import career_Navigator_parent.company.dto.request.UpdateCompanyRequest;
import career_Navigator_parent.company.dto.response.CompanyResponse;
import career_Navigator_parent.company.service.CompanyService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recruiter/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(
            @Valid
            @RequestBody
            CreateCompanyRequest request
    ) {

        CompanyResponse response =
                companyService.createCompany(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<CompanyResponse>
    getCurrentRecruiterCompany() {

        CompanyResponse response =
                companyService.getCurrentRecruiterCompany();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> getCompanyById(
            @PathVariable
            Long companyId
    ) {

        CompanyResponse response =
                companyService.getCompanyById(companyId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable
            Long companyId,

            @Valid
            @RequestBody
            UpdateCompanyRequest request
    ) {

        CompanyResponse response =
                companyService.updateCompany(
                        companyId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompany(
            @PathVariable
            Long companyId
    ) {

        companyService.deleteCompany(companyId);

        return ResponseEntity.noContent().build();
    }
}