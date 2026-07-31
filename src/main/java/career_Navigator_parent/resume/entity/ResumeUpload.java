package career_Navigator_parent.resume.entity;

import career_Navigator_parent.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "resume_uploads",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_resume_version",
                        columnNames = {
                                "student_id",
                                "resume_id",
                                "version"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_resume_upload_student"
            )
    )
    private Student student;

    /*
     * Connects the uploaded PDF/DOCX file
     * with the corresponding Resume record.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "resume_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_resume_upload_resume"
            )
    )
    private Resume resume;

    @Column(nullable = false, length = 255)
    private String originalFileName;

    @Column(nullable = false, unique = true, length = 255)
    private String storedFileName;

    @Column(nullable = false, length = 20)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false, length = 500)
    private String storagePath;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    private LocalDateTime uploadedAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        uploadedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (active == null) {
            active = true;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}