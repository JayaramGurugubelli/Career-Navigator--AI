package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.LearningBookmark;
import career_Navigator_parent.learning.enums.BookmarkTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LearningBookmarkRepository
        extends JpaRepository<LearningBookmark, Long> {

    Optional<LearningBookmark>
    findByStudentIdAndTargetTypeAndTargetId(
            Long studentId,
            BookmarkTargetType targetType,
            Long targetId
    );

    Page<LearningBookmark>
    findByStudentIdOrderByCreatedAtDesc(
            Long studentId,
            Pageable pageable
    );

    Page<LearningBookmark>
    findByStudentIdAndTargetType(
            Long studentId,
            BookmarkTargetType targetType,
            Pageable pageable
    );

    boolean existsByStudentIdAndTargetTypeAndTargetId(
            Long studentId,
            BookmarkTargetType targetType,
            Long targetId
    );

    void deleteByStudentIdAndTargetTypeAndTargetId(
            Long studentId,
            BookmarkTargetType targetType,
            Long targetId
    );
}