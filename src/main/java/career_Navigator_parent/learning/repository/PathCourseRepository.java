package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.PathCourse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PathCourseRepository
        extends JpaRepository<PathCourse, Long> {

    @EntityGraph(attributePaths = {
            "course",
            "milestone"
    })
    List<PathCourse>
    findByLearningPathIdAndActiveTrueOrderBySequenceNumberAsc(
            Long learningPathId
    );

    List<PathCourse>
    findByMilestoneIdAndActiveTrueOrderBySequenceNumberAsc(
            Long milestoneId
    );

    Optional<PathCourse>
    findByLearningPathIdAndCourseId(
            Long learningPathId,
            Long courseId
    );

    boolean existsByLearningPathIdAndCourseId(
            Long learningPathId,
            Long courseId
    );

    boolean existsByLearningPathIdAndSequenceNumber(
            Long learningPathId,
            Integer sequenceNumber
    );

    long countByLearningPathIdAndActiveTrue(
            Long learningPathId
    );
}