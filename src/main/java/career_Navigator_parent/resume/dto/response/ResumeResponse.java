package career_Navigator_parent.resume.dto.response;

import career_Navigator_parent.resume.enums.ResumeTemplate;

import lombok.*;


import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeResponse {


    private Long id;


    private String resumeTitle;


    private ResumeTemplate template;


    private Boolean defaultResume;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

}
