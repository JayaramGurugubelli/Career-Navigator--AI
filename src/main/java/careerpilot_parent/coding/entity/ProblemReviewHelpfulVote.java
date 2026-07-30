package careerpilot_parent.coding.entity;

import careerpilot_parent.common.entity.BaseEntity;
import careerpilot_parent.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "problem_review_helpful_votes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_review_helpful_student_review",
                        columnNames = {
                                "student_id",
                                "review_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_review_helpful_review",
                        columnList = "review_id"
                ),
                @Index(
                        name = "idx_review_helpful_student",
                        columnList = "student_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemReviewHelpfulVote
        extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_review_helpful_student"
            )
    )
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "review_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_review_helpful_review"
            )
    )
    private ProblemReview review;
}