package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.CareerDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CareerDomainRepository
        extends JpaRepository<CareerDomain, Long> {

    Optional<CareerDomain> findBySlugIgnoreCase(
            String slug
    );

    boolean existsBySlugIgnoreCase(
            String slug
    );

    boolean existsByNameIgnoreCase(
            String name
    );

    List<CareerDomain>
    findByActiveTrueOrderByDisplayOrderAscNameAsc();

    Page<CareerDomain> findByActive(
            Boolean active,
            Pageable pageable
    );
}