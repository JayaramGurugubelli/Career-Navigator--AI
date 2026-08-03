package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.LearningPathMilestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningPathMilestoneRepository
        extends JpaRepository<LearningPathMilestone, Long> {

    List<LearningPathMilestone>
    findByLearningPathIdAndActiveTrueOrderBySequenceNumberAsc(
            Long learningPathId
    );

    boolean existsByLearningPathIdAndSequenceNumber(
            Long learningPathId,
            Integer sequenceNumber
    );

    long countByLearningPathIdAndActiveTrue(
            Long learningPathId
    );
}