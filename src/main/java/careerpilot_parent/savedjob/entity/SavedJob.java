package careerpilot_parent.savedjob.entity;

import careerpilot_parent.common.entity.BaseEntity;
import careerpilot_parent.job.entity.JobPosting;
import careerpilot_parent.student.entity.Student;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "saved_jobs",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_saved_job_student_job",
                        columnNames = {
                                "student_id",
                                "job_posting_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_saved_job_student",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_saved_job_job",
                        columnList = "job_posting_id"
                ),
                @Index(
                        name = "idx_saved_job_saved_at",
                        columnList = "saved_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedJob extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_saved_job_student"
            )
    )
    private Student student;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "job_posting_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_saved_job_job_posting"
            )
    )
    private JobPosting jobPosting;

    @Column(
            name = "saved_at",
            nullable = false
    )
    private LocalDateTime savedAt;

    @Version
    private Long version;

    @PrePersist
    public void initializeSavedAt() {

        if (savedAt == null) {
            savedAt = LocalDateTime.now();
        }
    }
}