package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.StudentCourseProgress;
import career_Navigator_parent.learning.enums.ProgressStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentCourseProgressRepository
        extends JpaRepository<StudentCourseProgress, Long> {

    Optional<StudentCourseProgress>
    findByStudentIdAndCourseId(
            Long studentId,
            Long courseId
    );

    @EntityGraph(attributePaths = {
            "course"
    })
    Page<StudentCourseProgress>
    findByStudentIdOrderByLastAccessedAtDesc(
            Long studentId,
            Pageable pageable
    );

    List<StudentCourseProgress>
    findByStudentIdAndStatus(
            Long studentId,
            ProgressStatus status
    );

    long countByStudentIdAndStatus(
            Long studentId,
            ProgressStatus status
    );
}