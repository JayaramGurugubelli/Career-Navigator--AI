package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.AcademicDiscipline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcademicDisciplineRepository
        extends JpaRepository<AcademicDiscipline, Long> {

    Optional<AcademicDiscipline> findByCodeIgnoreCase(
            String code
    );

    Optional<AcademicDiscipline> findByNameIgnoreCase(
            String name
    );

    boolean existsByCodeIgnoreCase(
            String code
    );

    boolean existsByNameIgnoreCase(
            String name
    );

    List<AcademicDiscipline>
    findByActiveTrueOrderByDisplayOrderAscNameAsc();

    Page<AcademicDiscipline>
    findByActive(
            Boolean active,
            Pageable pageable
    );
}