package career_Navigator_parent.student.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEducationRequest {

    @NotBlank
    private String degree;

    @NotBlank
    private String specialization;

    @NotBlank
    private String institutionName;

    private String university;

    private String board;

    @NotNull
    private Integer passingYear;

    @NotNull
    private Double percentage;

    private String grade;

    @NotNull
    private Boolean pursuing;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;
}