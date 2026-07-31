package career_Navigator_parent.savedjob.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.job.entity.JobPosting;
import career_Navigator_parent.savedjob.enums.SavedJobAction;
import career_Navigator_parent.student.entity.Student;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "saved_job_history",
        indexes = {
                @Index(
                        name = "idx_saved_job_history_student",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_saved_job_history_job",
                        columnList = "job_posting_id"
                ),
                @Index(
                        name = "idx_saved_job_history_action",
                        columnList = "action"
                ),
                @Index(
                        name = "idx_saved_job_history_action_at",
                        columnList = "action_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedJobHistory extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_saved_job_history_student"
            )
    )
    private Student student;

    /*
     * Nullable so history can still conceptually remain
     * even when a job is archived or soft-deleted.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "job_posting_id",
            foreignKey = @ForeignKey(
                    name = "fk_saved_job_history_job_posting"
            )
    )
    private JobPosting jobPosting;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private SavedJobAction action;

    /*
     * Snapshot fields preserve useful history even when
     * the job title or company details are changed later.
     */
    @Column(
            name = "job_title_snapshot",
            nullable = false,
            length = 200
    )
    private String jobTitleSnapshot;

    @Column(
            name = "company_name_snapshot",
            nullable = false,
            length = 200
    )
    private String companyNameSnapshot;

    @Column(
            name = "location_snapshot",
            length = 200
    )
    private String locationSnapshot;

    @Column(
            name = "action_at",
            nullable = false
    )
    private LocalDateTime actionAt;

    @PrePersist
    public void initializeActionAt() {

        if (actionAt == null) {
            actionAt = LocalDateTime.now();
        }
    }
}