package career_Navigator_parent.student.dto.response;

import career_Navigator_parent.student.enums.ProjectType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProjectResponse {

    private Long id;

    private Long studentId;

    private String projectTitle;

    private ProjectType projectType;

    private String description;

    private String technologiesUsed;

    private String githubUrl;

    private String liveDemoUrl;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean currentlyWorking;
}