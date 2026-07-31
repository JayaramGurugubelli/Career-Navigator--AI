package career_Navigator_parent.student.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.student.enums.ProjectType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "student_projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProject extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_project_student")
    )
    private Student student;

    @NotBlank
    @Size(max = 150)
    @Column(name = "project_title", nullable = false, length = 150)
    private String projectTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_type", length = 30)
    private ProjectType projectType;

    @NotBlank
    @Size(max = 2000)
    @Column(nullable = false, length = 2000)
    private String description;

    @NotBlank
    @Size(max = 500)
    @Column(name = "technologies_used", nullable = false, length = 500)
    private String technologiesUsed;



    @Size(max = 500)
    @Pattern(
            regexp = "^(https?://).*$",
            message = "Must be a valid URL."
    )
    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Size(max = 500)
    @Pattern(
            regexp = "^(https?://).*$",
            message = "Must be a valid URL."
    )
    @Column(name = "live_demo_url", length = 500)
    private String liveDemoUrl;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder.Default
    @Column(name = "currently_working", nullable = false)
    private Boolean currentlyWorking = false;
}