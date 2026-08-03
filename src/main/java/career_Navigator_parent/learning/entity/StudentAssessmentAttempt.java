package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.AssessmentAttemptStatus;
import career_Navigator_parent.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "student_assessment_attempts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_assessment_attempt_number",
                        columnNames = {
                                "student_id",
                                "assessment_id",
                                "attempt_number"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_attempt_student",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_attempt_assessment",
                        columnList = "assessment_id"
                ),
                @Index(
                        name = "idx_attempt_status",
                        columnList = "student_id, status"
                ),
                @Index(
                        name = "idx_attempt_started",
                        columnList = "started_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAssessmentAttempt extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_assessment_attempt_student"
            )
    )
    private Student student;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "assessment_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_assessment_attempt_assessment"
            )
    )
    private Assessment assessment;

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
    private AssessmentAttemptStatus status =
            AssessmentAttemptStatus.STARTED;

    @Column(name = "score")
    private Double score;

    @Column(
            name = "percentage_score"
    )
    private Double percentageScore;

    @Column(
            name = "passed",
            nullable = false
    )
    @Builder.Default
    private Boolean passed = false;

    @Column(
            name = "correct_answers",
            nullable = false
    )
    @Builder.Default
    private Integer correctAnswers = 0;

    @Column(
            name = "wrong_answers",
            nullable = false
    )
    @Builder.Default
    private Integer wrongAnswers = 0;

    @Column(
            name = "unanswered_questions",
            nullable = false
    )
    @Builder.Default
    private Integer unansweredQuestions = 0;

    @Column(
            name = "started_at",
            nullable = false
    )
    private LocalDateTime startedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "evaluated_at")
    private LocalDateTime evaluatedAt;

    @OneToMany(
            mappedBy = "attempt",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<StudentAssessmentAnswer> answers =
            new ArrayList<>();

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    private void initialize() {
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }

        if (
                assessment != null
                        && assessment.getDurationMinutes() != null
        ) {
            expiresAt = startedAt.plusMinutes(
                    assessment.getDurationMinutes()
            );
        }

        if (attemptNumber == null || attemptNumber < 1) {
            attemptNumber = 1;
        }
    }

    public void addAnswer(
            StudentAssessmentAnswer answer
    ) {
        if (answer == null) {
            return;
        }

        answer.setAttempt(this);
        answers.add(answer);
    }

    public boolean isExpired() {
        return expiresAt != null
                && LocalDateTime.now()
                .isAfter(expiresAt);
    }

    public void submit() {
        if (isExpired()) {
            status = AssessmentAttemptStatus.EXPIRED;
            return;
        }

        status = AssessmentAttemptStatus.SUBMITTED;
        submittedAt = LocalDateTime.now();
    }

    public void completeEvaluation(
            double awardedScore,
            double maximumScore,
            int correct,
            int wrong,
            int unanswered
    ) {
        score = Math.max(awardedScore, 0.0);

        percentageScore =
                maximumScore <= 0
                        ? 0.0
                        : Math.min(
                        100.0,
                        score * 100.0 / maximumScore
                );

        correctAnswers = Math.max(correct, 0);
        wrongAnswers = Math.max(wrong, 0);
        unansweredQuestions =
                Math.max(unanswered, 0);

        passed =
                assessment != null
                        && percentageScore
                        >= assessment.getPassingScore();

        status =
                Boolean.TRUE.equals(passed)
                        ? AssessmentAttemptStatus.PASSED
                        : AssessmentAttemptStatus.FAILED;

        evaluatedAt = LocalDateTime.now();
    }
}