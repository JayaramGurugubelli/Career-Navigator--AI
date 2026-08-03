package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.LearningProject;
import career_Navigator_parent.learning.enums.LearningLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningProjectRepository
        extends JpaRepository<LearningProject, Long> {

    List<LearningProject>
    findByCourseIdAndActiveTrueOrderByIdAsc(
            Long courseId
    );

    List<LearningProject>
    findByMilestoneIdAndActiveTrueOrderByIdAsc(
            Long milestoneId
    );

    Page<LearningProject>
    findByDifficultyAndActiveTrue(
            LearningLevel difficulty,
            Pageable pageable
    );

    long countByCourseIdAndActiveTrue(
            Long courseId
    );

    long countByMilestoneLearningPathIdAndActiveTrue(
            Long learningPathId
    );
}