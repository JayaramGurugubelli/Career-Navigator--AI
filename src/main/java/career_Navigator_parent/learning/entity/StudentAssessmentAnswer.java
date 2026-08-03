package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "student_assessment_answers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_attempt_question_answer",
                        columnNames = {
                                "attempt_id",
                                "question_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_answer_attempt",
                        columnList = "attempt_id"
                ),
                @Index(
                        name = "idx_answer_question",
                        columnList = "question_id"
                ),
                @Index(
                        name = "idx_answer_correct",
                        columnList = "attempt_id, correct_answer"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAssessmentAnswer extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "attempt_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_student_answer_attempt"
            )
    )
    private StudentAssessmentAttempt attempt;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "question_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_student_answer_question"
            )
    )
    private AssessmentQuestion question;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "student_answer_selected_options",
            joinColumns = @JoinColumn(
                    name = "student_answer_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "assessment_option_id"
            ),
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_student_answer_selected_option",
                            columnNames = {
                                    "student_answer_id",
                                    "assessment_option_id"
                            }
                    )
            }
    )
    @Builder.Default
    private Set<AssessmentOption> selectedOptions =
            new LinkedHashSet<>();

    @Lob
    @Column(
            name = "text_answer",
            columnDefinition = "LONGTEXT"
    )
    private String textAnswer;

    @Column(
            name = "numeric_answer"
    )
    private Double numericAnswer;

    @Column(
            name = "file_url",
            length = 1500
    )
    private String fileUrl;

    @Column(
            name = "correct_answer"
    )
    private Boolean correct;

    @Column(
            name = "marks_awarded"
    )
    private Double marksAwarded;

    @Lob
    @Column(
            name = "evaluator_feedback",
            columnDefinition = "TEXT"
    )
    private String evaluatorFeedback;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Column(name = "evaluated_at")
    private LocalDateTime evaluatedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    @PreUpdate
    private void normalize() {

        if (answeredAt == null) {
            answeredAt = LocalDateTime.now();
        }

        if (
                marksAwarded != null
                        && marksAwarded < 0
        ) {
            marksAwarded = 0.0;
        }
    }

    public void markEvaluated(
            boolean correctAnswer,
            double awardedMarks,
            String feedback
    ) {
        correct = correctAnswer;
        marksAwarded = Math.max(awardedMarks, 0.0);
        evaluatorFeedback = feedback;
        evaluatedAt = LocalDateTime.now();
    }
}