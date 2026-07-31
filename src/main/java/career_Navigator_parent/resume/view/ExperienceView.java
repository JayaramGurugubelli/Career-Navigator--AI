package career_Navigator_parent.resume.view;

import lombok.Getter;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceView {

    private String companyName;

    private String jobTitle;

    private String employmentType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean currentlyWorking;

    private String technologies;

    private String description;

}