package career_Navigator_parent.student.entity;

import career_Navigator_parent.student.enums.LanguageProficiency;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "student_languages",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_language",
                        columnNames = {
                                "student_id",
                                "language_name"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private Student student;

    @Column(nullable = false, length = 50)
    private String languageName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LanguageProficiency proficiency;

}

