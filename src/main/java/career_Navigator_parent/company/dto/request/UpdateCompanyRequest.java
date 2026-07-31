package career_Navigator_parent.company.dto.request;

import career_Navigator_parent.company.enums.CompanySize;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompanyRequest {

    @Size(
            min = 2,
            max = 150,
            message = "Company name must contain between 2 and 150 characters."
    )
    private String name;

    @Size(
            max = 5000,
            message = "Company description must not exceed 5000 characters."
    )
    private String description;

    @Size(
            min = 2,
            max = 120,
            message = "Industry must contain between 2 and 120 characters."
    )
    private String industry;

    private CompanySize companySize;

    @Size(
            max = 500,
            message = "Website URL must not exceed 500 characters."
    )
    private String websiteUrl;

    @Size(
            max = 1000,
            message = "Logo URL must not exceed 1000 characters."
    )
    private String logoUrl;

    @Size(
            max = 255,
            message = "Headquarters must not exceed 255 characters."
    )
    private String headquarters;

    @Min(
            value = 1800,
            message = "Founded year must be 1800 or later."
    )
    @Max(
            value = 2100,
            message = "Founded year must not exceed 2100."
    )
    private Integer foundedYear;

    @Email(
            message = "Contact email must be valid."
    )
    @Size(
            max = 150,
            message = "Contact email must not exceed 150 characters."
    )
    private String contactEmail;

    @Size(
            max = 20,
            message = "Contact phone must not exceed 20 characters."
    )
    private String contactPhone;
}