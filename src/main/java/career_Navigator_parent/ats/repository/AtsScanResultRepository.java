package career_Navigator_parent.ats.repository;

import career_Navigator_parent.ats.entity.AtsScanResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AtsScanResultRepository
        extends JpaRepository<AtsScanResult, Long> {

    List<AtsScanResult> findByStudentIdOrderByCreatedAtDesc(
            Long studentId
    );

    Optional<AtsScanResult> findByIdAndStudentId(
            Long scanId,
            Long studentId
    );
}