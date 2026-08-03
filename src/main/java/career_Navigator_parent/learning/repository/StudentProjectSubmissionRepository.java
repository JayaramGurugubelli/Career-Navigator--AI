package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.StudentProjectSubmission;
import career_Navigator_parent.learning.enums.ProjectSubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentProjectSubmissionRepository
        extends JpaRepository<StudentProjectSubmission, Long> {

    Optional<StudentProjectSubmission>
    findTopByStudentIdAndProjectIdOrderByAttemptNumberDesc(
            Long studentId,
            Long projectId
    );

    Page<StudentProjectSubmission>
    findByStudentIdOrderBySubmittedAtDesc(
            Long studentId,
            Pageable pageable
    );

    Page<StudentProjectSubmission>
    findByStatus(
            ProjectSubmissionStatus status,
            Pageable pageable
    );

    long countByStudentIdAndProjectId(
            Long studentId,
            Long projectId
    );
}