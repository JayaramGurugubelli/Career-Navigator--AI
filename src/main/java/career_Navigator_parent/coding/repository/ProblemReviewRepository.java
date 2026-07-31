package career_Navigator_parent.coding.repository;
import career_Navigator_parent.coding.entity.ProblemReview;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface ProblemReviewRepository extends JpaRepository<ProblemReview,Long>{

 Optional<ProblemReview> findByStudentIdAndProblemId(Long sid,Long pid);

 @Query("select coalesce(avg(r.rating),0) from ProblemReview r where r.problem.id=:id")
 double averageRating(@Param("id") Long id);


 Optional<ProblemReview>
 findByIdAndProblemId(
         Long reviewId,
         Long problemId
 );

 Optional<ProblemReview>
 findByIdAndProblemIdAndStudentId(
         Long reviewId,
         Long problemId,
         Long studentId
 );

 @EntityGraph(attributePaths = {
         "student",
         "problem"
 })
 Page<ProblemReview>
 findByProblemIdOrderByHelpfulCountDescCreatedAtDesc(
         Long problemId,
         Pageable pageable
 );
}
