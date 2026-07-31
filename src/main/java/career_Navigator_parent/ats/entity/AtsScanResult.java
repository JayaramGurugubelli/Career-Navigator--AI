package career_Navigator_parent.ats.entity;

import career_Navigator_parent.resume.entity.Resume;
import career_Navigator_parent.student.entity.Student;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ats_scan_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtsScanResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_ats_scan_student"
            )
    )
    private Student student;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "resume_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_ats_scan_resume"
            )
    )
    private Resume resume;

    @Column(
            nullable = false,
            length = 150
    )
    private String jobTitle;

    @Column(length = 150)
    private String companyName;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String jobDescription;

    @Column(nullable = false)
    private Double atsScore;

    @Column(columnDefinition = "TEXT")
    private String matchedSkills;

    @Column(columnDefinition = "TEXT")
    private String missingSkills;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}