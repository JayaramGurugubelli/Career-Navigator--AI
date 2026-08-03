package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.LearningCertificate;
import career_Navigator_parent.learning.enums.CertificateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LearningCertificateRepository
        extends JpaRepository<LearningCertificate, Long> {

    @EntityGraph(attributePaths = {
            "student",
            "course",
            "learningPath",
            "assessment",
            "project"
    })
    Optional<LearningCertificate>
    findByVerificationCodeAndRevokedFalse(
            String verificationCode
    );

    Page<LearningCertificate>
    findByStudentIdAndRevokedFalseOrderByIssuedAtDesc(
            Long studentId,
            Pageable pageable
    );

    Page<LearningCertificate>
    findByStudentIdAndCertificateTypeAndRevokedFalse(
            Long studentId,
            CertificateType certificateType,
            Pageable pageable
    );

    boolean existsByCertificateNumber(
            String certificateNumber
    );

    boolean existsByVerificationCode(
            String verificationCode
    );
}