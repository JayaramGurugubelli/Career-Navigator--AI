package career_Navigator_parent.coding.repository;

import career_Navigator_parent.coding.entity.ProblemTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProblemTagRepository
        extends JpaRepository<ProblemTag, Long> {

    List<ProblemTag> findAllByIdIn(
            Collection<Long> ids
    );

    List<ProblemTag> findAllByIdInAndActiveTrue(
            Collection<Long> ids
    );

    List<ProblemTag> findAllByActiveTrueOrderByNameAsc();

    Optional<ProblemTag> findBySlug(String slug);

    Optional<ProblemTag> findBySlugAndActiveTrue(String slug);

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    boolean existsByNameIgnoreCaseAndIdNot(
            String name,
            Long id
    );

    boolean existsBySlugAndIdNot(
            String slug,
            Long id
    );

    long countByIdIn(Set<Long> ids);
}