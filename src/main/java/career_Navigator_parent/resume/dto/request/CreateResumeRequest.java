package career_Navigator_parent.resume.dto.request;



import career_Navigator_parent.resume.enums.ResumeTemplate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateResumeRequest {


    @NotBlank
    private String resumeTitle;


    @NotNull
    private ResumeTemplate template;


    @NotNull
    private Boolean defaultResume;

}