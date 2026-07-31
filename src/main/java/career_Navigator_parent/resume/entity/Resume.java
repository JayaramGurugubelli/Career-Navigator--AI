package career_Navigator_parent.resume.entity;
import career_Navigator_parent.student.entity.Student;

import career_Navigator_parent.resume.enums.ResumeTemplate;
import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;



    @Column(nullable = false)
    private String resumeTitle;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResumeTemplate template;



    @Column(nullable = false)
    private Boolean defaultResume;



    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;



    @PrePersist
    public void onCreate(){

        createdAt = LocalDateTime.now();

        updatedAt = LocalDateTime.now();
    }



    @PreUpdate
    public void onUpdate(){

        updatedAt = LocalDateTime.now();
    }

}