package career_Navigator_parent.company.dto.response;

import career_Navigator_parent.company.enums.CompanySize;
import career_Navigator_parent.company.enums.CompanyStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponse {

    private Long id;

    private String name;

    private String slug;

    private String description;

    private String industry;

    private CompanySize companySize;

    private String websiteUrl;

    private String logoUrl;

    private String headquarters;

    private Integer foundedYear;

    private String contactEmail;

    private String contactPhone;

    private CompanyStatus status;

    private Boolean verified;

    private Long ownerId;

    private String ownerUsername;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}