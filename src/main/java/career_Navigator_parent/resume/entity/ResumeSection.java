package career_Navigator_parent.resume.entity;

import career_Navigator_parent.resume.enums.ResumeSectionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "resume_sections",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_resume_section_reference",
                        columnNames = {
                                "resume_id",
                                "section_type",
                                "reference_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ResumeSectionType sectionType;


    /**
     * ID of StudentEducation /
     * StudentExperience /
     * StudentProject /
     * StudentSkill /
     * StudentCertification /
     * StudentAchievement
     */
    @Column(nullable = false)
    private Long referenceId;


    /**
     * Order in which this item appears
     * inside its section.
     */
    @Column(nullable = false)
    private Integer displayOrder;


    /**
     * Whether this item should appear
     * in the generated resume.
     */
    @Column(nullable = false)
    private Boolean visible;


    /**
     * Optional custom heading.
     *
     * Example:
     * Academic Projects
     * Personal Projects
     */
    @Column(length = 100)
    private String sectionHeading;


    /**
     * Highlight this item.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean highlight = false;


    /**
     * Override the original description
     * only for this resume.
     */
    @Column(length = 3000)
    private String customDescription;

}