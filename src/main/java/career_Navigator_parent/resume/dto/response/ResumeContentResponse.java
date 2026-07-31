package career_Navigator_parent.resume.dto.response;


import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeContentResponse {


    private Long id;


    private Long resumeId;


    private String professionalSummary;


    private String careerObjective;


    private String additionalInformation;


    private String linkedinUrl;


    private String githubUrl;


    private String portfolioUrl;

}