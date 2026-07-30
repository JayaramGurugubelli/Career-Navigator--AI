package careerpilot_parent.coding.repository;

import careerpilot_parent.coding.entity.ProblemImportedItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemImportedItemRepository
        extends JpaRepository<ProblemImportedItem, Long> {

    List<ProblemImportedItem>
    findByImportJobIdOrderByProblemIndexAsc(Long importJobId);
}