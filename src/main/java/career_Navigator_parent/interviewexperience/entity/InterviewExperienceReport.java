package career_Navigator_parent.interviewexperience.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.interviewexperience.enums.InterviewExperienceReportReason;
import career_Navigator_parent.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "interview_experience_reports",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_experience_report_user",
                        columnNames = {
                                "interview_experience_id",
                                "reported_by_user_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_experience_report_experience",
                        columnList = "interview_experience_id"
                ),
                @Index(
                        name = "idx_experience_report_user",
                        columnList = "reported_by_user_id"
                ),
                @Index(
                        name = "idx_experience_report_reviewed",
                        columnList = "reviewed"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewExperienceReport extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "interview_experience_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_experience_report_experience"
            )
    )
    private InterviewExperience interviewExperience;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "reported_by_user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_experience_report_user"
            )
    )
    private User reportedBy;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "reason",
            nullable = false,
            length = 50
    )
    private InterviewExperienceReportReason reason;

    @Column(
            name = "additional_details",
            length = 1000
    )
    private String additionalDetails;

    @Column(
            name = "reviewed",
            nullable = false
    )
    @Builder.Default
    private Boolean reviewed = false;

    @Column(
            name = "resolved",
            nullable = false
    )
    @Builder.Default
    private Boolean resolved = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reviewed_by_user_id",
            foreignKey = @ForeignKey(
                    name = "fk_experience_report_reviewer"
            )
    )
    private User reviewedBy;

    @Column(
            name = "reviewed_at"
    )
    private LocalDateTime reviewedAt;

    @Column(
            name = "admin_notes",
            length = 1000
    )
    private String adminNotes;

    public void markReviewed(
            User admin,
            String adminNotes,
            boolean resolved
    ) {

        this.reviewed = true;
        this.resolved = resolved;
        this.reviewedBy = admin;
        this.adminNotes = adminNotes;
        this.reviewedAt = LocalDateTime.now();
    }
}