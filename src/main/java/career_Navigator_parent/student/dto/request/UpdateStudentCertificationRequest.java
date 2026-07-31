package career_Navigator_parent.student.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStudentCertificationRequest {


    @NotBlank
    private String certificationName;


    @NotBlank
    private String issuingOrganization;


    private LocalDate issueDate;


    private LocalDate expiryDate;


    private String credentialId;


    private String credentialUrl;


    private String description;

}