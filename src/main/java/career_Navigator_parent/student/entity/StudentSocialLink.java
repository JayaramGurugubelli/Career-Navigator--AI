package career_Navigator_parent.student.entity;

import career_Navigator_parent.student.enums.SocialPlatform;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "student_social_links",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_platform",
                        columnNames = {
                                "student_id",
                                "platform"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSocialLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private Student student;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SocialPlatform platform;


    @Column(nullable = false, length = 500)
    private String url;


    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 1;


    @Column(nullable = false)
    @Builder.Default
    private Boolean visible = true;

}