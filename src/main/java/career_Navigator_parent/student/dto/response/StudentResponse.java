package career_Navigator_parent.student.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {

    private Long id;

    private Long userId;

    private String collegeName;

    private String universityName;

    private String degree;

    private String branch;

    private Double cgpa;

    private Integer graduationYear;

    private Integer currentSemester;

    private Integer backlogs;

    private Boolean activelyLooking;

    private Boolean profileCompleted;
}