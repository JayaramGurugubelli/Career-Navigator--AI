package careerpilot_parent.coding.repository;

import careerpilot_parent.coding.entity.ProblemTestCase;
import careerpilot_parent.coding.enums.TestCaseVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProblemTestCaseRepository
        extends JpaRepository<ProblemTestCase, Long> {

    Optional<ProblemTestCase> findByIdAndProblemId(
            Long testCaseId,
            Long problemId
    );

    Optional<ProblemTestCase> findByIdAndProblemIdAndActiveTrue(
            Long testCaseId,
            Long problemId
    );

    Page<ProblemTestCase> findAllByProblemIdAndActiveTrue(
            Long problemId,
            Pageable pageable
    );

    Page<ProblemTestCase> findAllByProblemId(
            Long problemId,
            Pageable pageable
    );

    Page<ProblemTestCase>
    findAllByProblemIdAndVisibilityAndActiveTrue(
            Long problemId,
            TestCaseVisibility visibility,
            Pageable pageable
    );

    Page<ProblemTestCase>
    findAllByProblemIdAndVisibility(
            Long problemId,
            TestCaseVisibility visibility,
            Pageable pageable
    );

    List<ProblemTestCase> findAllByIdInAndProblemIdAndActiveTrue(
            Collection<Long> testCaseIds,
            Long problemId
    );

    List<ProblemTestCase> findAllByProblemIdAndActiveTrueOrderByDisplayOrderAsc(
            Long problemId
    );

    boolean existsByProblemIdAndDisplayOrder(
            Long problemId,
            Integer displayOrder
    );

    boolean existsByProblemIdAndDisplayOrderAndIdNot(
            Long problemId,
            Integer displayOrder,
            Long testCaseId
    );

    boolean existsByProblemIdAndInputHashAndActiveTrue(
            Long problemId,
            String inputHash
    );



    long countByProblemIdAndActiveTrue(
            Long problemId
    );

    long countByProblemIdAndActiveFalse(
            Long problemId
    );

    long countByProblemIdAndVisibilityAndActiveTrue(
            Long problemId,
            TestCaseVisibility visibility
    );



    @Query("""
            select coalesce(sum(testCase.scoreWeight), 0.0)
            from ProblemTestCase testCase
            where testCase.problem.id = :problemId
              and testCase.active = true
            """)
    Double sumActiveScoreWeightByProblemId(
            @Param("problemId")
            Long problemId
    );
    @Query("""
        select coalesce(max(testCase.displayOrder), 0)
        from ProblemTestCase testCase
        where testCase.problem.id = :problemId
        """)
    Integer findMaximumDisplayOrderByProblemId(
            @Param("problemId")
            Long problemId
    );

    long countByProblemId(Long problemId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update ProblemTestCase testCase
        set testCase.active = false
        where testCase.problem.id = :problemId
          and testCase.active = true
        """)
    int deactivateAllByProblemId(
            @Param("problemId")
            Long problemId
    );
    @Query("""
        select coalesce(max(testCase.displayOrder), 0)
        from ProblemTestCase testCase
        where testCase.problem.id = :problemId
          and testCase.active = true
        """)
    Integer findMaximumActiveDisplayOrderByProblemId(
            @Param("problemId") Long problemId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update ProblemTestCase testCase
        set testCase.active = false
        where testCase.problem.id = :problemId
          and testCase.active = true
        """)
    int deactivateAllActiveByProblemId(
            @Param("problemId") Long problemId
    );

}