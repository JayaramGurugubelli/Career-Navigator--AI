package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.AssessmentStatus;
import career_Navigator_parent.learning.enums.AssessmentType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "LearningAssessment")
@Table(
        name = "learning_assessments",
        indexes = {
                @Index(
                        name = "idx_assessment_course",
                        columnList = "course_id"
                ),
                @Index(
                        name = "idx_assessment_milestone",
                        columnList = "milestone_id"
                ),
                @Index(
                        name = "idx_assessment_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_assessment_active",
                        columnList = "active"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assessment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "course_id",
            foreignKey = @ForeignKey(
                    name = "fk_assessment_course"
            )
    )
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "milestone_id",
            foreignKey = @ForeignKey(
                    name = "fk_assessment_milestone"
            )
    )
    private LearningPathMilestone milestone;

    @Column(
            nullable = false,
            length = 250
    )
    private String title;

    @Lob
    @Column(
            columnDefinition = "TEXT"
    )
    private String description;

    @Lob
    @Column(
            name = "instructions",
            columnDefinition = "TEXT"
    )
    private String instructions;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "assessment_type",
            nullable = false,
            length = 40
    )
    private AssessmentType assessmentType;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private AssessmentStatus status =
            AssessmentStatus.DRAFT;

    @Column(
            name = "passing_score",
            nullable = false
    )
    @Builder.Default
    private Double passingScore = 60.0;

    @Column(
            name = "maximum_score",
            nullable = false
    )
    @Builder.Default
    private Double maximumScore = 100.0;

    @Column(
            name = "maximum_attempts",
            nullable = false
    )
    @Builder.Default
    private Integer maximumAttempts = 3;

    @Column(
            name = "duration_minutes"
    )
    private Integer durationMinutes;

    @Column(
            name = "shuffle_questions",
            nullable = false
    )
    @Builder.Default
    private Boolean shuffleQuestions = false;

    @Column(
            name = "shuffle_options",
            nullable = false
    )
    @Builder.Default
    private Boolean shuffleOptions = false;

    @Column(
            name = "show_result_immediately",
            nullable = false
    )
    @Builder.Default
    private Boolean showResultImmediately = true;

    @Column(
            name = "show_correct_answers",
            nullable = false
    )
    @Builder.Default
    private Boolean showCorrectAnswers = false;

    @Column(
            name = "negative_marking_enabled",
            nullable = false
    )
    @Builder.Default
    private Boolean negativeMarkingEnabled = false;

    @Column(
            name = "negative_marks_per_wrong_answer"
    )
    private Double negativeMarksPerWrongAnswer;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean mandatory = true;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;

    @OneToMany(
            mappedBy = "assessment",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("sequenceNumber ASC")
    @Builder.Default
    private List<AssessmentQuestion> questions =
            new ArrayList<>();

    @Version
    @Column(nullable = false)
    private Long version;

    public void addQuestion(
            AssessmentQuestion question
    ) {
        if (question == null) {
            return;
        }

        question.setAssessment(this);
        questions.add(question);
    }

    public void removeQuestion(
            AssessmentQuestion question
    ) {
        if (question == null) {
            return;
        }

        questions.remove(question);
        question.setAssessment(null);
    }

    @PrePersist
    @PreUpdate
    private void validateAndNormalize() {

        if (title != null) {
            title = title.strip();
        }

        if (
                maximumScore == null
                        || maximumScore <= 0
        ) {
            maximumScore = 100.0;
        }

        if (
                passingScore == null
                        || passingScore < 0
                        || passingScore > maximumScore
        ) {
            passingScore =
                    Math.min(60.0, maximumScore);
        }

        if (
                maximumAttempts == null
                        || maximumAttempts < 1
        ) {
            maximumAttempts = 1;
        }

        if (
                durationMinutes != null
                        && durationMinutes < 1
        ) {
            durationMinutes = 1;
        }

        if (
                Boolean.TRUE.equals(
                        negativeMarkingEnabled
                )
                        && (
                        negativeMarksPerWrongAnswer == null
                                || negativeMarksPerWrongAnswer < 0
                )
        ) {
            negativeMarksPerWrongAnswer = 0.0;
        }

        if (status == null) {
            status = AssessmentStatus.DRAFT;
        }

        if (shuffleQuestions == null) {
            shuffleQuestions = false;
        }

        if (shuffleOptions == null) {
            shuffleOptions = false;
        }

        if (showResultImmediately == null) {
            showResultImmediately = true;
        }

        if (showCorrectAnswers == null) {
            showCorrectAnswers = false;
        }

        if (negativeMarkingEnabled == null) {
            negativeMarkingEnabled = false;
        }

        if (mandatory == null) {
            mandatory = true;
        }

        if (active == null) {
            active = true;
        }

        if (
                course == null
                        && milestone == null
        ) {
            throw new IllegalStateException(
                    "An assessment must belong to a course or milestone."
            );
        }
    }
}