package career_Navigator_parent.coding.repository;
import career_Navigator_parent.coding.entity.ProblemSolution;
import career_Navigator_parent.coding.enums.ProgrammingLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProblemSolutionRepository extends JpaRepository<ProblemSolution,Long>{
    List<ProblemSolution> findByProblemIdAndActiveTrueOrderByOfficialDescIdAsc(Long id);
    Optional<ProblemSolution> findByIdAndProblemId(
            Long solutionId,
            Long problemId
    );

    Optional<ProblemSolution>
    findByIdAndProblemIdAndActiveTrue(
            Long solutionId,
            Long problemId
    );

    Optional<ProblemSolution>
    findFirstByProblemIdAndOfficialTrueAndActiveTrueOrderByUpdatedAtDesc(
            Long problemId
    );

    List<ProblemSolution>
    findAllByProblemIdAndActiveTrueOrderByOfficialDescCreatedAtAsc(
            Long problemId
    );

    List<ProblemSolution>
    findAllByProblemIdOrderByOfficialDescCreatedAtAsc(
            Long problemId
    );

    boolean existsByProblemIdAndProgrammingLanguageAndTitleIgnoreCaseAndActiveTrue(
            Long problemId,
            ProgrammingLanguage language,
            String title
    );

    boolean existsByProblemIdAndProgrammingLanguageAndTitleIgnoreCaseAndIdNotAndActiveTrue(
            Long problemId,
            ProgrammingLanguage language,
            String title,
            Long solutionId
    );

}
