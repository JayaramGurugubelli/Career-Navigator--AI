package career_Navigator_parent.student.entity;




import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "student_achievements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAchievements {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;


    @Column(nullable = false)
    private String title;


    @Column(nullable = false)
    private String organization;


    private LocalDate achievementDate;


    @Column(length = 1000)
    private String description;


    private String certificateUrl;

}