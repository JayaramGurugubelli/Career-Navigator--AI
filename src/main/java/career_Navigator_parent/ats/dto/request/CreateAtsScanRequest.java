package career_Navigator_parent.ats.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAtsScanRequest {


    @NotNull(message = "Resume id is required")
    private Long resumeId;



    @NotBlank(message = "Job title is required")
    private String jobTitle;



    @NotBlank(message = "Company name is required")
    private String companyName;



    @NotBlank(message = "Job description is required")
    private String jobDescription;


}