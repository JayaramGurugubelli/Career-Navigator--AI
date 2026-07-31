package career_Navigator_parent.coding.entity;
import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="coding_problem_reviews",uniqueConstraints=@UniqueConstraint(name="uk_review_student_problem",columnNames={"student_id","problem_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProblemReview extends BaseEntity{
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="student_id",nullable=false) private Student student;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="problem_id",nullable=false) private CodingProblem problem;
 @Column(nullable=false) private Integer rating;
 @Column(length=120) private String title;
 @Column(nullable=false,columnDefinition="TEXT") private String review;
 @Builder.Default @Column(nullable=false) private Boolean containsSpoiler=false;
 @Builder.Default @Column(nullable=false) private Long helpfulCount=0L;
}
