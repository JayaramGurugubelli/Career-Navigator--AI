package career_Navigator_parent.assessment.entity;

import career_Navigator_parent.assessment.enums.AssessmentMode;
import career_Navigator_parent.assessment.enums.AssessmentProvider;
import career_Navigator_parent.assessment.enums.AssessmentResult;
import career_Navigator_parent.assessment.enums.AssessmentStatus;
import career_Navigator_parent.assessment.enums.AssessmentType;
import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.company.entity.RecruiterProfile;
import career_Navigator_parent.job.entity.JobApplication;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "assessments",
        indexes = {
                @Index(
                        name = "idx_assessment_application",
                        columnList = "job_application_id"
                ),
                @Index(
                        name = "idx_assessment_recruiter",
                        columnList = "recruiter_id"
                ),
                @Index(
                        name = "idx_assessment_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_assessment_scheduled_at",
                        columnList = "scheduled_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assessment extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "job_application_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_assessment_job_application"
            )
    )
    private JobApplication jobApplication;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "recruiter_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_assessment_recruiter"
            )
    )
    private RecruiterProfile recruiter;

    @Column(
            nullable = false,
            length = 150
    )
    private String title;

    @Column(
            columnDefinition = "TEXT"
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "assessment_type",
            nullable = false,
            length = 40
    )
    private AssessmentType assessmentType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "assessment_mode",
            nullable = false,
            length = 40
    )
    private AssessmentMode assessmentMode;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 40
    )
    private AssessmentProvider provider;

    @Column(
            name = "external_assessment_id",
            length = 150
    )
    private String externalAssessmentId;

    @Column(
            name = "assessment_url",
            length = 1000
    )
    private String assessmentUrl;

    @Column(
            name = "scheduled_at",
            nullable = false
    )
    private LocalDateTime scheduledAt;

    @Column(
            name = "available_until",
            nullable = false
    )
    private LocalDateTime availableUntil;

    @Column(
            name = "duration_minutes",
            nullable = false
    )
    private Integer durationMinutes;

    @Column(
            name = "maximum_score",
            nullable = false
    )
    private Double maximumScore;

    @Column(
            name = "passing_score",
            nullable = false
    )
    private Double passingScore;

    @Column(
            name = "obtained_score"
    )
    private Double obtainedScore;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private AssessmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private AssessmentResult result;

    @Column(
            columnDefinition = "TEXT"
    )
    private String instructions;

    @Column(
            name = "result_notes",
            columnDefinition = "TEXT"
    )
    private String resultNotes;

    @Column(
            name = "started_at"
    )
    private LocalDateTime startedAt;

    @Column(
            name = "submitted_at"
    )
    private LocalDateTime submittedAt;

    @Column(
            name = "completed_at"
    )
    private LocalDateTime completedAt;

    @Column(
            name = "cancelled_at"
    )
    private LocalDateTime cancelledAt;
}