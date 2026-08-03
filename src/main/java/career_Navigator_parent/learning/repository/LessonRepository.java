package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.Lesson;
import career_Navigator_parent.learning.enums.LessonType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonRepository
        extends JpaRepository<Lesson, Long> {

    List<Lesson>
    findByModuleIdAndActiveTrueOrderBySequenceNumberAsc(
            Long moduleId
    );

    Optional<Lesson> findByIdAndModuleId(
            Long lessonId,
            Long moduleId
    );

    boolean existsByModuleIdAndSequenceNumber(
            Long moduleId,
            Integer sequenceNumber
    );

    long countByModuleCourseIdAndActiveTrue(
            Long courseId
    );

    Page<Lesson>
    findByLessonTypeAndActiveTrue(
            LessonType lessonType,
            Pageable pageable
    );
}