package career_Navigator_parent.student.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEducationResponse {

    private Long id;

    private String degree;

    private String specialization;

    private String institutionName;

    private String university;

    private String board;

    private Integer passingYear;

    private Double percentage;

    private String grade;

    private Boolean pursuing;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;
}