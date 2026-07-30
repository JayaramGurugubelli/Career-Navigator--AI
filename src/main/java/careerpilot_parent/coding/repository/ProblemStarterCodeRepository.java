package careerpilot_parent.coding.repository;

import careerpilot_parent.coding.entity.ProblemStarterCode;
import careerpilot_parent.coding.enums.ProgrammingLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemStarterCodeRepository
        extends JpaRepository<ProblemStarterCode, Long> {

    Optional<ProblemStarterCode>
    findByIdAndProblemId(
            Long starterCodeId,
            Long problemId
    );

    Optional<ProblemStarterCode>
    findByProblemIdAndProgrammingLanguage(
            Long problemId,
            ProgrammingLanguage programmingLanguage
    );

    Optional<ProblemStarterCode>
    findByProblemIdAndProgrammingLanguageAndActiveTrue(
            Long problemId,
            ProgrammingLanguage programmingLanguage
    );

    List<ProblemStarterCode>
    findAllByProblemIdOrderByProgrammingLanguageAsc(
            Long problemId
    );

    List<ProblemStarterCode>
    findAllByProblemIdAndActiveTrueOrderByProgrammingLanguageAsc(
            Long problemId
    );

    boolean existsByProblemIdAndProgrammingLanguage(
            Long problemId,
            ProgrammingLanguage programmingLanguage
    );

    boolean existsByProblemIdAndProgrammingLanguageAndIdNot(
            Long problemId,
            ProgrammingLanguage programmingLanguage,
            Long starterCodeId
    );

}