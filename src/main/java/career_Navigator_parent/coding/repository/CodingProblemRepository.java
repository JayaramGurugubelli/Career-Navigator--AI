package career_Navigator_parent.coding.repository;

import career_Navigator_parent.coding.entity.CodingProblem;
import career_Navigator_parent.coding.enums.ProblemAttemptStatus;
import career_Navigator_parent.coding.enums.ProblemDifficulty;
import career_Navigator_parent.coding.enums.ProblemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CodingProblemRepository extends JpaRepository<CodingProblem, Long>, JpaSpecificationExecutor<CodingProblem> {

 Optional<CodingProblem> findBySlug(String slug);

 Optional<CodingProblem> findBySlugAndStatusAndActiveTrue(
         String slug,
         ProblemStatus status
 );

 Optional<CodingProblem> findByIdAndStatusAndActiveTrue(
         Long id,
         ProblemStatus status
 );

 boolean existsBySlug(String slug);

 @Query("""
        SELECT DISTINCT p
        FROM CodingProblem p
        LEFT JOIN p.tags t
        WHERE p.status = :status
          AND p.active = true
          AND (
                :keyword IS NULL
                OR LOWER(p.title)
                    LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(p.description)
                    LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
          AND (
                :difficulty IS NULL
                OR p.difficulty = :difficulty
              )
          AND (
                :tag IS NULL
                OR LOWER(t.slug) = LOWER(:tag)
              )
        """)
 Page<CodingProblem> searchPublished(
         @Param("status") ProblemStatus status,
         @Param("keyword") String keyword,
         @Param("difficulty") ProblemDifficulty difficulty,
         @Param("tag") String tag,
         Pageable pageable
 );
 long countByStatusAndActiveTrue(
         ProblemStatus status
 );

 long countByStatusAndActiveTrueAndDifficulty(
         ProblemStatus status,
         ProblemDifficulty difficulty
 );
 @Query("""
        select distinct p
        from CodingProblem p
        left join fetch p.tags t
        where p.status = :status
          and p.active = true
          and (:difficulty is null or p.difficulty = :difficulty)
          and not exists (
              select a.id
              from ProblemAttempt a
              where a.problem.id = p.id
                and a.student.id = :studentId
                and a.status = :solvedStatus
          )
        order by
          case
              when p.difficulty = :preferredDifficulty then 0
              else 1
          end,
          p.acceptedSubmissions desc,
          p.id desc
        """)
 List<CodingProblem> findRecommendations(
         @Param("studentId") Long studentId,
         @Param("status") ProblemStatus status,
         @Param("solvedStatus") ProblemAttemptStatus solvedStatus,
         @Param("difficulty") ProblemDifficulty difficulty,
         @Param("preferredDifficulty") ProblemDifficulty preferredDifficulty,
         Pageable pageable
 );
}
