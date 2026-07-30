package careerpilot_parent.coding.repository;

import careerpilot_parent.coding.entity.ProblemImportJob;
import careerpilot_parent.coding.enums.ProblemImportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemImportJobRepository
        extends JpaRepository<ProblemImportJob, Long> {

    Optional<ProblemImportJob> findByImportReference(String importReference);

    boolean existsByImportReference(String importReference);

    List<ProblemImportJob> findByStatusIn(
            List<ProblemImportStatus> statuses
    );
}