package career_Navigator_parent.student.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.student.enums.ProficiencyLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(
        name = "student_skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_skill",
                        columnNames = {
                                "student_id",
                                "skill_name"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSkill extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_skill_student")
    )
    private Student student;

    @NotBlank
    @Size(max = 100)
    @Column(name = "skill_name", nullable = false, length = 100)
    private String skillName;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "proficiency_level",
            nullable = false,
            length = 20
    )
    private ProficiencyLevel proficiencyLevel;

    @Min(0)
    @Max(50)
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Size(max = 500)
    @Column(length = 500)
    private String description;
}