package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.LearningResource;
import career_Navigator_parent.learning.enums.ProviderType;
import career_Navigator_parent.learning.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningResourceRepository
        extends JpaRepository<LearningResource, Long> {

    List<LearningResource>
    findByCourseIdAndActiveTrueOrderBySequenceNumberAsc(
            Long courseId
    );

    List<LearningResource>
    findByLessonIdAndActiveTrueOrderBySequenceNumberAsc(
            Long lessonId
    );

    List<LearningResource>
    findByMilestoneIdAndActiveTrueOrderBySequenceNumberAsc(
            Long milestoneId
    );

    List<LearningResource>
    findByProjectIdAndActiveTrueOrderBySequenceNumberAsc(
            Long projectId
    );

    Page<LearningResource>
    findByResourceTypeAndActiveTrue(
            ResourceType resourceType,
            Pageable pageable
    );

    Page<LearningResource>
    findByProviderTypeAndActiveTrue(
            ProviderType providerType,
            Pageable pageable
    );
}