package career_Navigator_parent.auth.service.impl;
import career_Navigator_parent.auth.entity.PasswordResetToken;
import career_Navigator_parent.auth.exception.TokenExpiredException;
import career_Navigator_parent.auth.repository.PasswordResetTokenRepository;
import career_Navigator_parent.auth.service.PasswordResetService;
import career_Navigator_parent.common.exception.InvalidTokenException;
import career_Navigator_parent.common.exception.UserNotFoundException;
import career_Navigator_parent.user.entity.User;
import career_Navigator_parent.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetServiceImpl implements PasswordResetService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    /**
     * Create Password Reset Token
     */
    @Override
    public void createResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        // Delete old reset token if exists
        passwordResetTokenRepository
                .findByUser(user)
                .ifPresent(
                        passwordResetTokenRepository::delete
                );
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();
        passwordResetTokenRepository.save(resetToken);



        // Later integrate Email Service here
        // Send reset link/email

    }





    /**
     * Reset Password
     */
    @Override
    public void resetPassword(
            String token,
            String newPassword
    ) {



        PasswordResetToken resetToken =

                passwordResetTokenRepository
                        .findByToken(token)

                        .orElseThrow(
                                () -> new InvalidTokenException(
                                        "Invalid reset token"
                                )
                        );




        // Check already used token
        if(Boolean.TRUE.equals(
                resetToken.getUsed()
        )) {


            throw new InvalidTokenException(
                    "Reset token already used"
            );

        }




        // Check expiry
        if(resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {


            throw new TokenExpiredException(
                    "Reset token expired"
            );

        }




        User user =
                resetToken.getUser();




        // Update password
        user.setPassword(
                passwordEncoder.encode(
                        newPassword
                )
        );



        userRepository.save(
                user
        );




        // Disable token after use
        resetToken.setUsed(true);



        passwordResetTokenRepository.save(
                resetToken
        );

    }

}