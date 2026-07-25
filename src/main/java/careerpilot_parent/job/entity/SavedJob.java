package careerpilot_parent.job.entity;

import careerpilot_parent.common.entity.BaseEntity;
import careerpilot_parent.student.entity.Student;

import jakarta.persistence.*;

import lombok.*;

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
                    name = "fk_saved_job_posting"
            )
    )
    private JobPosting jobPosting;
}