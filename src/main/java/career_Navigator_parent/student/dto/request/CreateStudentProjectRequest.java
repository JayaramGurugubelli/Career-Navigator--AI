package career_Navigator_parent.student.dto.request;

import career_Navigator_parent.student.enums.ProjectType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStudentProjectRequest {

    @NotBlank
    @Size(max = 150)
    private String projectTitle;

    private ProjectType projectType;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @NotBlank
    @Size(max = 500)
    private String technologiesUsed;

    @Size(max = 500)
    private String githubUrl;

    @Size(max = 500)
    private String liveDemoUrl;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull
    private Boolean currentlyWorking;
}