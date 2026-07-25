package careerpilot_parent.company.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterProfileResponse {

    private Long id;

    private Long userId;

    private String designation;

    private String officialEmail;

    private String phoneNumber;

    private String linkedinUrl;

    private boolean verified;

    private boolean active;

    private CompanyResponse company;
}
