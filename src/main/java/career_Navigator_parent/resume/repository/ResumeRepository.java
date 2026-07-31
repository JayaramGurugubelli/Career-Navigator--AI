package career_Navigator_parent.resume.repository;

import career_Navigator_parent.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository
        extends JpaRepository<Resume, Long> {

    /**
     * Get all resumes belonging to a student.
     */
    List<Resume> findByStudentId(Long studentId);

    /**
     * Find a resume while also checking student ownership.
     */
    Optional<Resume> findByIdAndStudentId(
            Long resumeId,
            Long studentId
    );

    /**
     * Find the student's default resume.
     */
    Optional<Resume> findByStudentIdAndDefaultResumeTrue(
            Long studentId
    );
}