package career_Navigator_parent.resume.dto.request;


import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateResumeContentRequest {


    @Size(max = 2000)
    private String professionalSummary;



    @Size(max = 2000)
    private String careerObjective;



    @Size(max = 1000)
    private String additionalInformation;



    @Size(max = 255)
    private String linkedinUrl;



    @Size(max = 255)
    private String githubUrl;



    @Size(max = 255)
    private String portfolioUrl;

}