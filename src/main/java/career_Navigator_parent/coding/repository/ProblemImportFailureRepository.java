package career_Navigator_parent.coding.repository;

import career_Navigator_parent.coding.entity.ProblemImportFailure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemImportFailureRepository
        extends JpaRepository<ProblemImportFailure, Long> {

    List<ProblemImportFailure>
    findByImportJobIdOrderByProblemIndexAsc(Long importJobId);
}