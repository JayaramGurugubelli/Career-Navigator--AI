package careerpilot_parent.company.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRecruiterProfileRequest {

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotBlank(message = "Designation is required")
    @Size(max = 120)
    private String designation;

    @NotBlank(message = "Official email is required")
    @Email(message = "Official email must be valid")
    @Size(max = 150)
    private String officialEmail;

    @Size(max = 20)
    private String phoneNumber;

    @Size(max = 500)
    private String linkedinUrl;
}