package careerpilot_parent.auth.service.impl;


import careerpilot_parent.audit.annotation.Auditable;
import careerpilot_parent.audit.enums.AuditAction;
import careerpilot_parent.audit.enums.AuditEntityType;
import careerpilot_parent.auth.entity.PasswordResetToken;
import careerpilot_parent.auth.entity.VerificationToken;
import careerpilot_parent.auth.exception.EmailAlreadyVerifiedException;
import careerpilot_parent.auth.exception.TokenExpiredException;
import careerpilot_parent.auth.repository.PasswordResetTokenRepository;
import careerpilot_parent.auth.repository.VerificationTokenRepository;
import careerpilot_parent.auth.service.EmailService;
import careerpilot_parent.auth.service.PasswordResetService;
import careerpilot_parent.auth.service.VerificationService;
import careerpilot_parent.common.exception.InvalidTokenException;
import careerpilot_parent.common.exception.UserNotFoundException;
import careerpilot_parent.user.entity.User;
import careerpilot_parent.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VerificationServiceImpl implements VerificationService {


    private final VerificationTokenRepository verificationTokenRepository;

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetService;
    @Value("${app.base-url}")
    private String baseUrl;


    @Override
    public void createVerificationToken(User user) {


        verificationTokenRepository
                .findByUser(user)
                .ifPresent(
                        verificationTokenRepository::delete
                );


        VerificationToken token =
                VerificationToken.builder()

                        .token(
                                UUID.randomUUID().toString()
                        )

                        .user(user)

                        .expiryDate(
                                LocalDateTime.now()
                                        .plusHours(24)
                        )

                        .used(false)

                        .build();



        VerificationToken savedToken = verificationTokenRepository.save(token);

        String verificationLink =
                baseUrl + "/api/auth/verify-email?token=" + savedToken.getToken();

        emailService.sendVerificationEmail(
                user.getEmail(),
                user.getFirstName(),
                verificationLink
        );

    }



    @Override
    @Transactional
    @Auditable(
            action = AuditAction.EMAIL_VERIFIED,
            entityType = AuditEntityType.AUTHENTICATION,
            description = "User email was verified",
            captureArguments = false,
            captureResponse = false
    )
    public void verifyEmail(String token){


        VerificationToken verificationToken =
                verificationTokenRepository
                        .findByTokenAndUsedFalse(token)
                        .orElseThrow(
                                () -> new InvalidTokenException(
                                        "Invalid verification token"
                                )
                        );



        if(Boolean.TRUE.equals(
                verificationToken.getUsed()
        )) {


            throw new InvalidTokenException(
                    "Token already used"
            );

        }



        if(verificationToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {


            throw new TokenExpiredException(
                    "Token expired"
            );

        }



        User user =
                verificationToken.getUser();



        if(Boolean.TRUE.equals(
                user.getEmailVerified()
        )) {


            throw new EmailAlreadyVerifiedException(
                    "Email already verified"
            );

        }
        user.setEmailVerified(true);
        verificationToken.setUsed(true);
        userRepository.save(user);
        verificationTokenRepository.save(verificationToken);
    }



    @Override
    @Transactional
    @Auditable(
            action = AuditAction.VERIFICATION_EMAIL_RESENT,
            entityType = AuditEntityType.AUTHENTICATION,
            description = "Email verification link was resent",
            captureArguments = false,
            captureResponse = false
    )
    public void resendVerificationToken(String email){
        User user =
                userRepository.findByEmail(email)

                        .orElseThrow(
                                () -> new UserNotFoundException(
                                        "User not found"
                                )
                        );

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new EmailAlreadyVerifiedException(
                    "Email is already verified."
            );
        }
        createVerificationToken(user);

    }
    @Override
    public VerificationToken getByToken(String token) {
        return verificationTokenRepository.findByToken(token).orElseThrow(() -> new InvalidTokenException("Invalid verification token."));
    }

    @Override
    public void markAsUsed(VerificationToken token) {token.setUsed(true);
        verificationTokenRepository.save(token);
    }
    @Override
    public void createPasswordResetToken(User user) {
        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        passwordResetService.save(token);

        // send email here
    }
    @Override
    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetService.findByToken(token).orElseThrow(() -> new InvalidTokenException("Invalid password reset token."));
    }
    @Override
    public void markPasswordResetTokenAsUsed(PasswordResetToken token) {
        token.setUsed(true);
        passwordResetService.save(token);
    }
}