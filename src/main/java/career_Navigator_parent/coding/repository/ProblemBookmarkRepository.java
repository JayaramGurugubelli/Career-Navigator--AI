package career_Navigator_parent.coding.repository;

import career_Navigator_parent.coding.entity.ProblemBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemBookmarkRepository
        extends JpaRepository<ProblemBookmark, Long> {

    boolean existsByStudentIdAndProblemId(
            Long studentId,
            Long problemId
    );

    Optional<ProblemBookmark> findByStudentIdAndProblemId(
            Long studentId,
            Long problemId
    );

    long countByStudentId(Long studentId);

    List<ProblemBookmark> findByStudentIdOrderByCreatedAtDesc(
            Long studentId
    );

    void deleteByStudentIdAndProblemId(
            Long studentId,
            Long problemId
    );

}