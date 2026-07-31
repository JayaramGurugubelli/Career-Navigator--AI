package career_Navigator_parent.auth.repository;

import career_Navigator_parent.auth.entity.RefreshToken;
import career_Navigator_parent.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);



    List<RefreshToken> findByUserAndRevokedFalse(User user);

    List<RefreshToken> findByUserAndExpiredFalse(User user);
    Optional<RefreshToken> findByUser(User user);
    void deleteByUser(User user);

    void deleteByExpiryDateBefore(LocalDateTime dateTime);


    List<RefreshToken> findAllByUser(User user);


    void deleteAllByUser(User user);

    boolean existsByToken(String token);
}
