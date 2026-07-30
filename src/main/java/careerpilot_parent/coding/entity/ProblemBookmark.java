package careerpilot_parent.coding.entity;

import careerpilot_parent.common.entity.BaseEntity;
import careerpilot_parent.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "coding_problem_bookmarks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_problem_bookmark_student_problem",
                        columnNames = {"student_id", "problem_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_problem_bookmark_student",
                        columnList = "student_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemBookmark extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_problem_bookmark_student"
            )
    )
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "problem_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_problem_bookmark_problem"
            )
    )
    private CodingProblem problem;

    @Column(length = 500)
    private String notes;
}