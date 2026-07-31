package career_Navigator_parent.resume.view;

import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationView {
    private String certificationName;

    private String issuingOrganization;

    private LocalDate issueDate;

    private String credentialId;

}
