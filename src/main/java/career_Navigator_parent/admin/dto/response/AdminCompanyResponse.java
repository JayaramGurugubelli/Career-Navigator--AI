package career_Navigator_parent.admin.dto.response;

import career_Navigator_parent.company.enums.CompanySize;
import career_Navigator_parent.company.enums.CompanyStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCompanyResponse {

    private Long id;
    private String name;
    private String website;
    private String industry;
    private String headquarters;
    private CompanySize companySize;
    private CompanyStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
