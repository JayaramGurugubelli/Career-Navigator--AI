package careerpilot_parent.resume.repository;

import careerpilot_parent.resume.entity.ResumeUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeUploadRepository
        extends JpaRepository<ResumeUpload, Long> {

    List<ResumeUpload> findByStudentIdOrderByVersionDesc(
            Long studentId
    );

    Optional<ResumeUpload> findByIdAndStudentId(
            Long uploadId,
            Long studentId
    );

    Optional<ResumeUpload>
    findFirstByStudentIdAndActiveTrueOrderByVersionDesc(
            Long studentId
    );

    List<ResumeUpload>
    findByStudentIdAndResumeIdOrderByVersionDesc(
            Long studentId,
            Long resumeId
    );

    Optional<ResumeUpload>
    findFirstByStudentIdAndResumeIdAndActiveTrueOrderByVersionDesc(
            Long studentId,
            Long resumeId
    );
}