package careerpilot_parent.company.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRecruiterProfileRequest {

    /*
     * Supply this when the company already exists.
     */
    private Long existingCompanyId;

    /*
     * Supply this when creating a new company.
     */
    @Valid
    private CreateCompanyRequest company;

    @NotBlank(message = "Designation is required")
    @Size(
            max = 120,
            message = "Designation cannot exceed 120 characters"
    )
    private String designation;

    @NotBlank(message = "Official email is required")
    @Email(message = "Official email must be valid")
    @Size(
            max = 150,
            message = "Official email cannot exceed 150 characters"
    )
    private String officialEmail;

    @Size(
            max = 20,
            message = "Phone number cannot exceed 20 characters"
    )
    private String phoneNumber;

    @Size(
            max = 500,
            message = "LinkedIn URL cannot exceed 500 characters"
    )
    private String linkedinUrl;
}