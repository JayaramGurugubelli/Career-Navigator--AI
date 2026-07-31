package career_Navigator_parent.student.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStudentAchievementRequest {


    @NotBlank
    private String title;


    @NotBlank
    private String organization;


    private LocalDate achievementDate;


    private String description;


    private String certificateUrl;

}