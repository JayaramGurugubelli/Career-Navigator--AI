package careerpilot_parent.job.entity;

import careerpilot_parent.common.entity.BaseEntity;
import careerpilot_parent.company.enums.ApplicationStatus;
import careerpilot_parent.resume.entity.Resume;
import careerpilot_parent.student.entity.Student;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "job_applications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_job_application_student_job",
                        columnNames = {
                                "student_id",
                                "job_posting_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_job_application_student",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_job_application_job",
                        columnList = "job_posting_id"
                ),
                @Index(
                        name = "idx_job_application_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "job_posting_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_application_job_posting"
            )
    )
    private JobPosting jobPosting;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_application_student"
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
                    name = "fk_application_resume"
            )
    )
    private Resume resume;

    @Column(
            name = "cover_letter",
            columnDefinition = "TEXT"
    )
    private String coverLetter;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 40
    )
    @Builder.Default
    private ApplicationStatus status =
            ApplicationStatus.SUBMITTED;

    @Column(
            name = "applied_at",
            nullable = false
    )
    private LocalDateTime appliedAt;

    @Column(
            name = "last_status_changed_at",
            nullable = false
    )
    private LocalDateTime lastStatusChangedAt;

    @Column(
            name = "withdrawn_at"
    )
    private LocalDateTime withdrawnAt;

    @Column(
            name = "recruiter_notes",
            columnDefinition = "TEXT"
    )
    private String recruiterNotes;

    @Version
    private Long version;
}