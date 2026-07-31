package career_Navigator_parent.student.entity;
import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_student_user")
    )
    private User user;

    @Size(max = 150)
    @Column(name = "college_name")
    private String collegeName;

    @Size(max = 100)
    @Column(name = "university_name")
    private String universityName;

    @Size(max = 100)
    private String degree;

    @Size(max = 100)
    private String branch;

    @Column
    private Double cgpa;

    @Min(1900)
    @Max(2100)
    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Min(1)
    @Max(12)
    @Column(name = "current_semester")
    private Integer currentSemester;

    @Min(0)
    private Integer backlogs;

    @Builder.Default
    @Column(name = "actively_looking", nullable = false)
    private Boolean activelyLooking = true;

    @Builder.Default
    @Column(name = "profile_completed", nullable = false)
    private Boolean profileCompleted = false;
    @OneToMany(
            mappedBy = "student",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<StudentEducation> educations = new HashSet<>();
    @OneToMany(
            mappedBy = "student",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<StudentExperience> experiences = new HashSet<>();
}
