package careerpilot_parent.user.repository;

import careerpilot_parent.shared.enums.RoleName;
import careerpilot_parent.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("""
           SELECT DISTINCT u
           FROM User u
           LEFT JOIN FETCH u.roles ur
           LEFT JOIN FETCH ur.role
           WHERE u.email = :value
              OR u.username = :value
           """)
    Optional<User> findByEmailOrUsernameWithRoles(
            @Param("value") String value
    );

    Optional<User> findByEmailOrUsername(
            String email,
            String username
    );

    @Query("""
           SELECT DISTINCT u
           FROM User u
           LEFT JOIN FETCH u.roles ur
           LEFT JOIN FETCH ur.role
           """)
    List<User> findAllWithRoles();

    @Query("""
           SELECT DISTINCT u
           FROM User u
           LEFT JOIN FETCH u.roles ur
           LEFT JOIN FETCH ur.role
           WHERE u.id = :userId
           """)
    Optional<User> findByIdWithRoles(
            @Param("userId") Long userId
    );

    @Query("""
           SELECT COUNT(DISTINCT u.id)
           FROM User u
           JOIN u.roles ur
           JOIN ur.role r
           WHERE r.name = :roleName
           """)
    long countByRoleName(
            @Param("roleName") RoleName roleName
    );
    boolean existsByEmailIgnoreCase(String email);
}
