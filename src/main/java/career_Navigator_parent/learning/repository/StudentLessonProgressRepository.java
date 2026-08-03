package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.StudentLessonProgress;
import career_Navigator_parent.learning.enums.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentLessonProgressRepository
        extends JpaRepository<StudentLessonProgress, Long> {

    Optional<StudentLessonProgress>
    findByStudentIdAndLessonId(
            Long studentId,
            Long lessonId
    );

    List<StudentLessonProgress>
    findByStudentIdAndCourseId(
            Long studentId,
            Long courseId
    );

    long countByStudentIdAndCourseIdAndStatus(
            Long studentId,
            Long courseId,
            ProgressStatus status
    );

    long countByStudentIdAndStatus(
            Long studentId,
            ProgressStatus status
    );
}