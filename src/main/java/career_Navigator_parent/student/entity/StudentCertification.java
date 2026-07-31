package career_Navigator_parent.student.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "student_certifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCertification {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;


    @Column(nullable = false)
    private String certificationName;


    @Column(nullable = false)
    private String issuingOrganization;


    private LocalDate issueDate;


    private LocalDate expiryDate;


    private String credentialId;


    private String credentialUrl;


    private String description;

}