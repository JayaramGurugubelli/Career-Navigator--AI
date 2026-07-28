package careerpilot_parent.seeder;

import careerpilot_parent.shared.enums.AccountStatus;
import careerpilot_parent.shared.enums.RoleName;
import careerpilot_parent.user.entity.Role;
import careerpilot_parent.user.entity.User;
import careerpilot_parent.user.entity.UserRole;
import careerpilot_parent.user.repository.RoleRepository;
import careerpilot_parent.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Override
    @Transactional
    public void run(String... args) {

        /*
         * Idempotent seeding:
         * restarting the application must not create duplicate admins.
         */
        if (userRepository.existsByEmailIgnoreCase(adminEmail)) {
            return;
        }

        Role adminRole = roleRepository
                .findByName(RoleName.ADMIN)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "ADMIN role is not available in the database."
                        )
                );

        User admin = User.builder()
                .firstName("System")
                .lastName("Administrator")
                .username(adminUsername)
                .email(adminEmail.toLowerCase())
                .phoneNumber("9494164037")
                .password(
                        passwordEncoder.encode(adminPassword)
                )
                .accountStatus(AccountStatus.ACTIVE)
                .emailVerified(true)
                .mobileVerified(true)
                .enabled(true)
                .build();

        UserRole adminUserRole = UserRole.builder()
                .user(admin)
                .role(adminRole)
                .build();

        admin.getRoles().add(adminUserRole);

        userRepository.save(admin);
    }
}