package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.CareerRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CareerRoleRepository
        extends JpaRepository<CareerRole, Long> {

    boolean existsBySlugIgnoreCase(
            String slug
    );

    @EntityGraph(attributePaths = {
            "domain",
            "eligibleDisciplines"
    })
    Optional<CareerRole> findBySlugIgnoreCase(
            String slug
    );


    @Query("""
            select distinct role
            from CareerRole role
            join role.eligibleDisciplines discipline
            where role.active = true
              and discipline.id = :disciplineId
            """)
    Page<CareerRole> findPublishedForDiscipline(
            @Param("disciplineId")
            Long disciplineId,
            Pageable pageable
    );

    Page<CareerRole> findByActiveTrue(
            Pageable pageable
    );

    Page<CareerRole> findByFeaturedTrueAndActiveTrue(
            Pageable pageable
    );
    @EntityGraph(attributePaths = {
            "domain",
            "eligibleDisciplines"
    })
    @Query("""
        select role
        from CareerRole role
        where role.id = :id
        """)
    Optional<CareerRole> findDetailedById(
            @Param("id")
            Long id
    );
}