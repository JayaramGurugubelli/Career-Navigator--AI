package career_Navigator_parent.coding.repository;

import career_Navigator_parent.coding.entity.ProblemTestCaseGenerationJob;
import career_Navigator_parent.coding.enums.TestCaseGenerationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemTestCaseGenerationJobRepository
        extends JpaRepository<
        ProblemTestCaseGenerationJob,
        Long
        > {

    Optional<ProblemTestCaseGenerationJob>
    findByIdAndProblemId(
            Long jobId,
            Long problemId
    );

    boolean existsByProblemIdAndStatusIn(
            Long problemId,
            List<TestCaseGenerationStatus> statuses
    );
}