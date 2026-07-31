package career_Navigator_parent.interview.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.company.entity.RecruiterProfile;
import career_Navigator_parent.interview.enums.InterviewMode;
import career_Navigator_parent.interview.enums.InterviewResult;
import career_Navigator_parent.interview.enums.InterviewStatus;
import career_Navigator_parent.interview.enums.InterviewType;
import career_Navigator_parent.job.entity.JobApplication;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "interviews",
        indexes = {
                @Index(
                        name = "idx_interview_application",
                        columnList = "job_application_id"
                ),
                @Index(
                        name = "idx_interview_recruiter",
                        columnList = "recruiter_id"
                ),
                @Index(
                        name = "idx_interview_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_interview_scheduled_at",
                        columnList = "scheduled_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "job_application_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_interview_job_application"
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
                    name = "fk_interview_recruiter"
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
            name = "interview_type",
            nullable = false,
            length = 40
    )
    private InterviewType interviewType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "interview_mode",
            nullable = false,
            length = 30
    )
    private InterviewMode interviewMode;

    @Column(
            name = "round_number",
            nullable = false
    )
    @Builder.Default
    private Integer roundNumber = 1;

    @Column(
            name = "scheduled_at",
            nullable = false
    )
    private LocalDateTime scheduledAt;

    @Column(
            name = "end_at",
            nullable = false
    )
    private LocalDateTime endAt;

    @Column(
            name = "duration_minutes",
            nullable = false
    )
    private Integer durationMinutes;

    @Column(
            name = "meeting_url",
            length = 1000
    )
    private String meetingUrl;

    @Column(
            name = "meeting_id",
            length = 150
    )
    private String meetingId;

    @Column(
            name = "meeting_password",
            length = 150
    )
    private String meetingPassword;

    @Column(
            name = "location",
            length = 500
    )
    private String location;

    @Column(
            name = "interviewer_name",
            nullable = false,
            length = 150
    )
    private String interviewerName;

    @Column(
            name = "interviewer_email",
            length = 150
    )
    private String interviewerEmail;

    @Column(
            name = "interviewer_designation",
            length = 150
    )
    private String interviewerDesignation;

    @Column(
            columnDefinition = "TEXT"
    )
    private String instructions;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private InterviewStatus status =
            InterviewStatus.SCHEDULED;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private InterviewResult result =
            InterviewResult.PENDING;

    @Column(
            name = "student_response_notes",
            columnDefinition = "TEXT"
    )
    private String studentResponseNotes;

    @Column(
            name = "feedback",
            columnDefinition = "TEXT"
    )
    private String feedback;

    @Column(
            name = "strengths",
            columnDefinition = "TEXT"
    )
    private String strengths;

    @Column(
            name = "areas_for_improvement",
            columnDefinition = "TEXT"
    )
    private String areasForImprovement;

    @Column(
            name = "technical_score"
    )
    private Double technicalScore;

    @Column(
            name = "communication_score"
    )
    private Double communicationScore;

    @Column(
            name = "problem_solving_score"
    )
    private Double problemSolvingScore;

    @Column(
            name = "overall_score"
    )
    private Double overallScore;

    @Column(
            name = "confirmed_at"
    )
    private LocalDateTime confirmedAt;

    @Column(
            name = "declined_at"
    )
    private LocalDateTime declinedAt;

    @Column(
            name = "completed_at"
    )
    private LocalDateTime completedAt;

    @Column(
            name = "cancelled_at"
    )
    private LocalDateTime cancelledAt;

    @Version
    private Long version;
}