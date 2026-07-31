package career_Navigator_parent.resume.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "resume_contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeContent {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;



    @Column(length = 2000)
    private String professionalSummary;



    @Column(length = 2000)
    private String careerObjective;



    @Column(length = 1000)
    private String additionalInformation;

    @Column(length = 1000)
    private String linkedinUrl;

    @Column(length = 1000)
    private String githubUrl;

    @Column(length = 1000)
    private String portfolioUrl;
}