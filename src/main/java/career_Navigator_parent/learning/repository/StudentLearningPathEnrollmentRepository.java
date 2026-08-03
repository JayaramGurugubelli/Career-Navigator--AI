package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.StudentLearningPathEnrollment;
import career_Navigator_parent.learning.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StudentLearningPathEnrollmentRepository
        extends JpaRepository<StudentLearningPathEnrollment, Long> {

    @EntityGraph(attributePaths = {
            "learningPath",
            "learningPath.careerRole",
            "currentMilestone"
    })
    Optional<StudentLearningPathEnrollment>
    findByStudentIdAndLearningPathId(
            Long studentId,
            Long learningPathId
    );

    @EntityGraph(attributePaths = {
            "learningPath",
            "learningPath.careerRole",
            "currentMilestone"
    })
    Page<StudentLearningPathEnrollment>
    findByStudentIdOrderByLastAccessedAtDesc(
            Long studentId,
            Pageable pageable
    );

    List<StudentLearningPathEnrollment> findByStudentIdAndStatus(
            Long studentId,
            EnrollmentStatus status
    );

    boolean existsByStudentIdAndLearningPathId(
            Long studentId,
            Long learningPathId
    );

    long countByStudentIdAndStatus(
            Long studentId,
            EnrollmentStatus status
    );

    @Query("""
            select case when count(enrollment) > 0 then true else false end
            from StudentLearningPathEnrollment enrollment
            join PathCourse pathCourse
              on pathCourse.learningPath = enrollment.learningPath
            where enrollment.student.id = :studentId
              and pathCourse.course.id = :courseId
              and pathCourse.active = true
              and enrollment.status in :statuses
            """)
    boolean existsAccessibleEnrollmentForCourse(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId,
            @Param("statuses") Collection<EnrollmentStatus> statuses
    );
}