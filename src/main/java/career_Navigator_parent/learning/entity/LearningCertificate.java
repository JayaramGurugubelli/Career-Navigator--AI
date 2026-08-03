package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.CertificateType;
import career_Navigator_parent.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "learning_certificates",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_learning_certificate_number",
                        columnNames = "certificate_number"
                ),
                @UniqueConstraint(
                        name = "uk_learning_certificate_verification",
                        columnNames = "verification_code"
                )
        },
        indexes = {
                @Index(
                        name = "idx_certificate_student",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_certificate_type",
                        columnList = "certificate_type"
                ),
                @Index(
                        name = "idx_certificate_issued",
                        columnList = "issued_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningCertificate extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "certificate_type",
            nullable = false,
            length = 40
    )
    private CertificateType certificateType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_path_id")
    private LearningPath learningPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private LearningProject project;

    @Column(
            name = "certificate_number",
            nullable = false,
            length = 100
    )
    private String certificateNumber;

    @Column(
            name = "verification_code",
            nullable = false,
            length = 100
    )
    private String verificationCode;

    @Column(
            name = "certificate_url",
            length = 1500
    )
    private String certificateUrl;

    @Column(
            name = "title",
            nullable = false,
            length = 250
    )
    private String title;

    @Column(
            name = "score"
    )
    private Double score;

    @Column(
            name = "issued_at",
            nullable = false
    )
    private LocalDateTime issuedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean revoked = false;

    @Lob
    @Column(
            name = "revocation_reason",
            columnDefinition = "TEXT"
    )
    private String revocationReason;

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    private void initialize() {
        if (issuedAt == null) {
            issuedAt = LocalDateTime.now();
        }

        validateTarget();
    }

    private void validateTarget() {
        int targetCount = 0;

        if (course != null) {
            targetCount++;
        }

        if (learningPath != null) {
            targetCount++;
        }

        if (assessment != null) {
            targetCount++;
        }

        if (project != null) {
            targetCount++;
        }

        if (targetCount != 1) {
            throw new IllegalStateException(
                    "A certificate must belong to exactly one learning target."
            );
        }
    }
}