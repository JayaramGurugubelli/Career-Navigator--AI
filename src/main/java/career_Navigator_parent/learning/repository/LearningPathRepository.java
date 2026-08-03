package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.LearningPath;
import career_Navigator_parent.learning.enums.ContentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LearningPathRepository
        extends JpaRepository<LearningPath, Long> {

    boolean existsBySlugIgnoreCaseAndPathVersion(
            String slug,
            Integer pathVersion
    );

    @EntityGraph(attributePaths = {
            "careerRole",
            "careerRole.domain",
            "disciplines"
    })
    Optional<LearningPath> findBySlugIgnoreCaseAndStatusAndActiveTrue(
            String slug,
            ContentStatus status
    );



    Page<LearningPath> findByStatusAndActiveTrue(
            ContentStatus status,
            Pageable pageable
    );

    Page<LearningPath>
    findByCareerRoleIdAndStatusAndActiveTrue(
            Long careerRoleId,
            ContentStatus status,
            Pageable pageable
    );

    @Query("""
            select distinct path
            from LearningPath path
            join path.disciplines discipline
            where path.status = :status
              and path.active = true
              and discipline.id = :disciplineId
            """)
    Page<LearningPath> findForDiscipline(
            @Param("disciplineId")
            Long disciplineId,

            @Param("status")
            ContentStatus status,

            Pageable pageable
    );
    @EntityGraph(attributePaths = {
            "careerRole",
            "careerRole.domain",
            "disciplines"
    })
    @Query("""
        select path
        from LearningPath path
        where path.id = :id
        """)
    Optional<LearningPath> findDetailedById(
            @Param("id")
            Long id
    );
}