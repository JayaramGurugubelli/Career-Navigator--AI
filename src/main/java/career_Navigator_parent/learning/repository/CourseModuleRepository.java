package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseModuleRepository
        extends JpaRepository<CourseModule, Long> {

    List<CourseModule>
    findByCourseIdAndActiveTrueOrderBySequenceNumberAsc(
            Long courseId
    );

    Optional<CourseModule>
    findByIdAndCourseId(
            Long moduleId,
            Long courseId
    );

    boolean existsByCourseIdAndSequenceNumber(
            Long courseId,
            Integer sequenceNumber
    );

    long countByCourseIdAndActiveTrue(
            Long courseId
    );
}