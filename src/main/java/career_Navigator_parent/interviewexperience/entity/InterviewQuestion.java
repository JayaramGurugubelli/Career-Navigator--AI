package career_Navigator_parent.interviewexperience.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.interviewexperience.enums.InterviewQuestionCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "interview_questions",
        indexes = {
                @Index(
                        name = "idx_interview_question_round",
                        columnList = "interview_round_id"
                ),
                @Index(
                        name = "idx_interview_question_category",
                        columnList = "category"
                ),
                @Index(
                        name = "idx_interview_question_topic",
                        columnList = "topic"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "interview_round_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_interview_question_round"
            )
    )
    private InterviewExperienceRound interviewRound;

    @Column(
            name = "question",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "category",
            nullable = false,
            length = 50
    )
    private InterviewQuestionCategory category;

    @Column(
            name = "topic",
            length = 150
    )
    private String topic;

    /*
     * Optional details such as what the interviewer expected,
     * follow-up questions or how the question was presented.
     */
    @Column(
            name = "additional_details",
            columnDefinition = "TEXT"
    )
    private String additionalDetails;

    @Column(
            name = "display_order",
            nullable = false
    )
    private Integer displayOrder;
}