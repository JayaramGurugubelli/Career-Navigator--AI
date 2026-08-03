package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "learning_assessment_options",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_question_option_sequence",
                        columnNames = {
                                "question_id",
                                "sequence_number"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_option_question",
                        columnList = "question_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentOption extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "question_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_option_question"
            )
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AssessmentQuestion question;

    @Lob
    @Column(
            name = "option_text",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String optionText;

    @Column(
            name = "image_url",
            length = 1500
    )
    private String imageUrl;

    @Column(
            name = "sequence_number",
            nullable = false
    )
    private Integer sequenceNumber;

    @Column(
            name = "correct_option",
            nullable = false
    )
    @Builder.Default
    private Boolean correctOption = false;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    @PreUpdate
    private void normalize() {

        if (
                sequenceNumber == null
                        || sequenceNumber < 1
        ) {
            sequenceNumber = 1;
        }

        if (correctOption == null) {
            correctOption = false;
        }

        if (active == null) {
            active = true;
        }
    }
}