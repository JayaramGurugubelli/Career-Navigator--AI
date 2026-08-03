package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.QuestionDifficulty;
import career_Navigator_parent.learning.enums.QuestionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "learning_assessment_questions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_assessment_question_sequence",
                        columnNames = {
                                "assessment_id",
                                "sequence_number"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_question_assessment",
                        columnList = "assessment_id"
                ),
                @Index(
                        name = "idx_question_type",
                        columnList = "question_type"
                ),
                @Index(
                        name = "idx_question_active",
                        columnList = "active"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentQuestion extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "assessment_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_question_assessment"
            )
    )
    private Assessment assessment;

    @Lob
    @Column(
            name = "question_text",
            nullable = false,
            columnDefinition = "LONGTEXT"
    )
    private String questionText;

    @Lob
    @Column(
            name = "question_context",
            columnDefinition = "LONGTEXT"
    )
    private String questionContext;

    @Column(
            name = "image_url",
            length = 1500
    )
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "question_type",
            nullable = false,
            length = 40
    )
    private QuestionType questionType;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    @Builder.Default
    private QuestionDifficulty difficulty =
            QuestionDifficulty.MEDIUM;

    @Column(
            name = "sequence_number",
            nullable = false
    )
    private Integer sequenceNumber;

    @Column(
            name = "marks",
            nullable = false
    )
    @Builder.Default
    private Double marks = 1.0;

    @Column(
            name = "negative_marks"
    )
    private Double negativeMarks;

    @Lob
    @Column(
            name = "expected_answer",
            columnDefinition = "LONGTEXT"
    )
    private String expectedAnswer;

    @Lob
    @Column(
            name = "answer_explanation",
            columnDefinition = "LONGTEXT"
    )
    private String answerExplanation;

    @Column(
            name = "case_sensitive",
            nullable = false
    )
    @Builder.Default
    private Boolean caseSensitive = false;

    @Column(
            name = "numeric_tolerance"
    )
    private Double numericTolerance;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("sequenceNumber ASC")
    @Builder.Default
    private List<AssessmentOption> options =
            new ArrayList<>();

    @Version
    @Column(nullable = false)
    private Long version;

    public void addOption(
            AssessmentOption option
    ) {
        if (option == null) {
            return;
        }

        option.setQuestion(this);
        options.add(option);
    }

    @PrePersist
    @PreUpdate
    private void validateAndNormalize() {

        if (
                sequenceNumber == null
                        || sequenceNumber < 1
        ) {
            sequenceNumber = 1;
        }

        if (marks == null || marks <= 0) {
            marks = 1.0;
        }

        if (
                negativeMarks != null
                        && negativeMarks < 0
        ) {
            negativeMarks = 0.0;
        }

        if (
                numericTolerance != null
                        && numericTolerance < 0
        ) {
            numericTolerance = 0.0;
        }

        if (difficulty == null) {
            difficulty = QuestionDifficulty.MEDIUM;
        }

        if (caseSensitive == null) {
            caseSensitive = false;
        }

        if (active == null) {
            active = true;
        }
    }
}