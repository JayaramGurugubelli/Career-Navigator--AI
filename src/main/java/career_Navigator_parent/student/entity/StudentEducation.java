package career_Navigator_parent.student.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "student_educations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEducation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_student_education_student")
    )
    private Student student;

    @Column(nullable = false, length = 100)
    private String degree;

    @Column(nullable = false, length = 100)
    private String specialization;

    @Column(nullable = false, length = 150)
    private String institutionName;

    @Column(length = 100)
    private String university;

    @Column(length = 100)
    private String board;

    @Min(1900)
    @Max(2100)
    @Column(nullable = false)
    private Integer passingYear;

    @Column(nullable = false)
    private Double percentage;

    @Size(max = 100)
    private String grade;

    @Column(nullable = false)
    private Boolean pursuing;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(length = 500)
    private String description;
}