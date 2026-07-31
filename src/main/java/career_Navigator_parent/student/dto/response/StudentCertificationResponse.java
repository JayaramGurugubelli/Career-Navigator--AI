package career_Navigator_parent.student.dto.response;


import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCertificationResponse {


    private Long id;


    private String certificationName;


    private String issuingOrganization;


    private LocalDate issueDate;


    private LocalDate expiryDate;


    private String credentialId;


    private String credentialUrl;


    private String description;

}