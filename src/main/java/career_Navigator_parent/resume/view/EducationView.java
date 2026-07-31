package career_Navigator_parent.resume.view;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationView {
    private String degree;
    private String institutionName;
    private String specialization;

    private String institution;

    private Integer passingYear;

    private Double percentage;

}
