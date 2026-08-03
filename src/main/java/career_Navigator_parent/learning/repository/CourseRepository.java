package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.Course;
import career_Navigator_parent.learning.enums.ContentStatus;
import career_Navigator_parent.learning.enums.CourseType;
import career_Navigator_parent.learning.enums.LearningLevel;
import career_Navigator_parent.learning.enums.ProviderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsBySlugIgnoreCaseAndCourseVersion(
            String slug,
            Integer courseVersion
    );

    @EntityGraph(attributePaths = {
            "disciplines",
            "prerequisites"
    })
    @Query("""
            select distinct course
            from Course course
            where course.id = :courseId
            """)
    Optional<Course> findDetailedById(
            @Param("courseId") Long courseId
    );

    Optional<Course> findByIdAndStatusAndActiveTrue(
            Long courseId,
            ContentStatus status
    );

    Optional<Course> findBySlugIgnoreCaseAndStatusAndActiveTrue(
            String slug,
            ContentStatus status
    );

    Page<Course> findByStatusAndActiveTrue(
            ContentStatus status,
            Pageable pageable
    );

    Page<Course> findByCourseTypeAndStatusAndActiveTrue(
            CourseType courseType,
            ContentStatus status,
            Pageable pageable
    );

    Page<Course> findByLevelAndStatusAndActiveTrue(
            LearningLevel level,
            ContentStatus status,
            Pageable pageable
    );

    Page<Course> findByProviderTypeAndStatusAndActiveTrue(
            ProviderType providerType,
            ContentStatus status,
            Pageable pageable
    );

    Page<Course> findByFeaturedTrueAndStatusAndActiveTrue(
            ContentStatus status,
            Pageable pageable
    );

    @Query("""
            select distinct course
            from Course course
            join course.disciplines discipline
            where course.status = :status
              and course.active = true
              and discipline.id = :disciplineId
            """)
    Page<Course> findForDiscipline(
            @Param("disciplineId") Long disciplineId,
            @Param("status") ContentStatus status,
            Pageable pageable
    );

    @Query("""
            select course
            from Course course
            where course.status = :status
              and course.active = true
              and lower(course.title)
                  like concat('%', lower(:search), '%')
            """)
    Page<Course> searchPublishedCourses(
            @Param("search") String search,
            @Param("status") ContentStatus status,
            Pageable pageable
    );
}
