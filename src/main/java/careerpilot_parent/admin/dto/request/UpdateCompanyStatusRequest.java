package careerpilot_parent.admin.dto.request;

import careerpilot_parent.company.enums.CompanyStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompanyStatusRequest {

    @NotNull(message = "Company status is required")
    private CompanyStatus status;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
}
