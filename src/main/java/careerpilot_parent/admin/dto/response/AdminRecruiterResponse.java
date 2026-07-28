package careerpilot_parent.admin.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminRecruiterResponse {

    private Long id;
    private Long userId;
    private String recruiterName;
    private String userEmail;
    private Long companyId;
    private String companyName;
    private String designation;
    private String officialEmail;
    private String phoneNumber;
    private String linkedinUrl;
    private boolean verified;
    private boolean active;
}
