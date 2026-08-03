package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.ProjectSubmissionStatus;
import career_Navigator_parent.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "student_project_submissions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_project_attempt",
                        columnNames = {
                                "student_id",
                                "project_id",
                                "attempt_number"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_project_submission_student",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_project_submission_project",
                        columnList = "project_id"
                ),
                @Index(
                        name = "idx_project_submission_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProjectSubmission
        extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private Student student;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "project_id",
            nullable = false
    )
    private LearningProject project;

    @Column(
            name = "attempt_number",
            nullable = false
    )
    private Integer attemptNumber;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private ProjectSubmissionStatus status =
            ProjectSubmissionStatus.DRAFT;

    @Lob
    @Column(
            name = "submission_text",
            columnDefinition = "LONGTEXT"
    )
    private String submissionText;

    @Column(
            name = "repository_url",
            length = 1500
    )
    private String repositoryUrl;

    @Column(
            name = "external_url",
            length = 1500
    )
    private String externalUrl;

    @Column(
            name = "file_url",
            length = 1500
    )
    private String fileUrl;

    @Column(
            name = "video_url",
            length = 1500
    )
    private String videoUrl;

    @Column(name = "score")
    private Double score;

    @Column(
            name = "passed",
            nullable = false
    )
    @Builder.Default
    private Boolean passed = false;

    @Lob
    @Column(
            name = "reviewer_feedback",
            columnDefinition = "LONGTEXT"
    )
    private String reviewerFeedback;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    public void submit() {
        status = ProjectSubmissionStatus.SUBMITTED;
        submittedAt = LocalDateTime.now();
    }

    public void startReview() {
        status = ProjectSubmissionStatus.UNDER_REVIEW;
    }

    public void approve(
            double awardedScore,
            String feedback
    ) {
        score = Math.max(awardedScore, 0.0);
        reviewerFeedback = feedback;

        passed =
                project != null
                        && score >= project.getPassingScore();

        status =
                Boolean.TRUE.equals(passed)
                        ? ProjectSubmissionStatus.APPROVED
                        : ProjectSubmissionStatus.CHANGES_REQUESTED;

        reviewedAt = LocalDateTime.now();
    }
}