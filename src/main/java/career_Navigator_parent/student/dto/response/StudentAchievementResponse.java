package career_Navigator_parent.student.dto.response;


import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAchievementResponse {


    private Long id;


    private String title;


    private String organization;


    private LocalDate achievementDate;


    private String description;


    private String certificateUrl;

}