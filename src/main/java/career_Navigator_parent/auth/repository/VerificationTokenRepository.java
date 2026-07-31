package career_Navigator_parent.auth.repository;

import career_Navigator_parent.auth.entity.VerificationToken;
import career_Navigator_parent.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUser(User user);

    void deleteByExpiryDateBefore(LocalDateTime dateTime);
    Optional<VerificationToken> findByTokenAndUsedFalse(String token);

}