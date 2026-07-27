package careerpilot_parent.company.repository;

import careerpilot_parent.company.entity.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecruiterProfileRepository
        extends JpaRepository<RecruiterProfile, Long> {

    Optional<RecruiterProfile> findByUserId(
            Long userId
    );

    Optional<RecruiterProfile> findByUserIdAndActiveTrue(
            Long userId
    );

    boolean existsByUserId(
            Long userId
    );

    boolean existsByOfficialEmailIgnoreCase(
            String officialEmail
    );
}