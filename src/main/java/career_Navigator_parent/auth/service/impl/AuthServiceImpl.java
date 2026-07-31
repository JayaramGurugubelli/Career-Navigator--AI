package career_Navigator_parent.auth.service.impl;

import career_Navigator_parent.audit.annotation.Auditable;
import career_Navigator_parent.audit.enums.AuditAction;
import career_Navigator_parent.audit.enums.AuditEntityType;
import career_Navigator_parent.auth.dto.request.ForgotPasswordRequest;
import career_Navigator_parent.auth.dto.request.LoginRequest;
import career_Navigator_parent.auth.dto.request.RecruiterRegisterRequest;
import career_Navigator_parent.auth.dto.request.RegisterRequest;
import career_Navigator_parent.auth.dto.request.ResetPasswordRequest;
import career_Navigator_parent.auth.dto.request.VerifyEmailRequest;

import career_Navigator_parent.auth.dto.response.LoginResponse;
import career_Navigator_parent.auth.dto.response.RegisterResponse;

import career_Navigator_parent.auth.entity.PasswordResetToken;
import career_Navigator_parent.auth.entity.RefreshToken;
import career_Navigator_parent.auth.entity.VerificationToken;

import career_Navigator_parent.auth.exception.EmailAlreadyVerifiedException;
import career_Navigator_parent.auth.exception.EmailNotVerifiedException;
import career_Navigator_parent.auth.exception.PasswordMismatchException;
import career_Navigator_parent.auth.exception.TokenExpiredException;

import career_Navigator_parent.auth.mapper.RecruiterRegistrationMapper;

import career_Navigator_parent.auth.service.AuthService;
import career_Navigator_parent.auth.service.RefreshTokenService;
import career_Navigator_parent.auth.service.VerificationService;

import career_Navigator_parent.common.exception.InvalidCredentialsException;
import career_Navigator_parent.common.exception.InvalidTokenException;
import career_Navigator_parent.common.exception.UserAlreadyExistsException;
import career_Navigator_parent.common.exception.UserNotFoundException;

import career_Navigator_parent.security.jwt.JwtService;

import career_Navigator_parent.shared.enums.AccountStatus;
import career_Navigator_parent.shared.enums.RoleName;

import career_Navigator_parent.user.entity.Role;
import career_Navigator_parent.user.entity.User;
import career_Navigator_parent.user.entity.UserProfile;
import career_Navigator_parent.user.entity.UserRole;

import career_Navigator_parent.user.mapper.UserMapper;

import career_Navigator_parent.user.repository.RoleRepository;
import career_Navigator_parent.user.repository.UserProfileRepository;
import career_Navigator_parent.user.repository.UserRepository;
import career_Navigator_parent.user.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final UserProfileRepository userProfileRepository;

    private final RoleRepository roleRepository;

    private final UserRoleRepository userRoleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final VerificationService verificationService;

    private final UserMapper userMapper;

    private final RecruiterRegistrationMapper
            recruiterRegistrationMapper;

    /*
     * Student registration
     */
    @Override
    @Transactional
    @Auditable(
            action = AuditAction.REGISTER,
            entityType = AuditEntityType.USER,
            description = "Student registered a new account",
            captureArguments = false,
            captureResponse = false
    )
    public RegisterResponse register(RegisterRequest request) {

        String normalizedEmail =
                normalizeEmail(request.getEmail());

        String normalizedUsername =
                normalizeUsername(
                        request.getUsername()
                );

        validateNewUser(
                normalizedEmail,
                normalizedUsername
        );

        User user =
                User.builder()
                        .firstName(
                                normalizeRequired(
                                        request.getFirstName()
                                )
                        )
                        .lastName(
                                normalizeRequired(
                                        request.getLastName()
                                )
                        )
                        .username(
                                normalizedUsername
                        )
                        .email(
                                normalizedEmail
                        )
                        .phoneNumber(
                                normalizeNullable(
                                        request.getPhoneNumber()
                                )
                        )
                        .build();

        User savedUser =
                createUserWithRole(
                        user,
                        request.getPassword(),
                        RoleName.STUDENT
                );

        return buildRegistrationResponse(
                savedUser,
                "Registration successful. " +
                        "Please check your email to verify your account."
        );
    }

    /*
     * Recruiter registration
     */
    @Override
    @Transactional
    @Auditable(
            action = AuditAction.REGISTER,
            entityType = AuditEntityType.RECRUITER,
            description = "Recruiter registered a new account",
            captureArguments = false,
            captureResponse = false
    )
    public RegisterResponse registerRecruiter(RecruiterRegisterRequest request) {

        String normalizedEmail =
                normalizeEmail(request.getEmail());

        String normalizedUsername =
                normalizeUsername(
                        request.getUsername()
                );

        validateNewUser(
                normalizedEmail,
                normalizedUsername
        );

        User user =
                recruiterRegistrationMapper
                        .toEntity(request);

        /*
         * Ensures values used for validation and storage
         * are exactly the same.
         */
        user.setEmail(normalizedEmail);
        user.setUsername(normalizedUsername);

        User savedUser =
                createUserWithRole(
                        user,
                        request.getPassword(),
                        RoleName.RECRUITER
                );

        return recruiterRegistrationMapper
                .toRegisterResponse(savedUser);
    }

    /*
     * Shared registration workflow.
     *
     * Future roles can reuse this method.
     */
    private User createUserWithRole(User user, String rawPassword, RoleName roleName) {

        prepareNewUser(
                user,
                rawPassword
        );

        User savedUser =
                userRepository.save(user);

        assignRole(
                savedUser,
                roleName
        );

        createUserProfile(savedUser);

        /*
         * Reuse the existing verification service.
         * It should create the token and send the email.
         */
        verificationService
                .createVerificationToken(savedUser);

        return savedUser;
    }

    private void prepareNewUser(User user, String rawPassword) {

        user.setPassword(
                passwordEncoder.encode(
                        rawPassword
                )
        );

        user.setEmailVerified(false);

        user.setEnabled(true);

        user.setAccountStatus(
                AccountStatus.ACTIVE
        );
    }

    private void assignRole(User user, RoleName roleName) {

        Role role =
                roleRepository
                        .findByName(roleName)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        roleName
                                                + " role is not configured "
                                                + "in the database."
                                )
                        );

        UserRole userRole =
                UserRole.builder()
                        .user(user)
                        .role(role)
                        .build();

        userRoleRepository.save(userRole);
    }

    private void createUserProfile(User user) {

        UserProfile userProfile =
                UserProfile.builder()
                        .user(user)
                        .build();

        userProfileRepository.save(
                userProfile
        );
    }

    private void validateNewUser(
            String email,
            String username
    ) {

        if (userRepository.existsByEmail(email)) {

            throw new UserAlreadyExistsException(
                    "An account already exists with this email."
            );
        }

        if (userRepository.existsByUsername(username)) {

            throw new UserAlreadyExistsException(
                    "Username is already taken."
            );
        }
    }

    private RegisterResponse buildRegistrationResponse(
            User user,
            String message
    ) {

        return RegisterResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .emailVerificationRequired(true)
                .message(message)
                .build();
    }

    /*
     * Login
     */
    @Override
    @Transactional
    @Auditable(
            action = AuditAction.LOGIN,
            entityType = AuditEntityType.AUTHENTICATION,
            description = "User logged into the application",
            captureArguments = false,
            captureResponse = false
    )
    public LoginResponse login(
            LoginRequest request
    ) {

        String usernameOrEmail =
                request.getUsernameOrEmail()
                        .trim();

        User user =
                findUserForLogin(
                        usernameOrEmail
                );

        if (!Boolean.TRUE.equals(
                user.getEmailVerified()
        )) {

            throw new EmailNotVerifiedException(
                    "Please verify your email first."
            );
        }

        if (!Boolean.TRUE.equals(
                user.getEnabled()
        )) {

            throw new InvalidCredentialsException(
                    "This account is disabled."
            );
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getUsername()
                );

        String accessToken =
                jwtService.generateAccessToken(
                        userDetails
                );

        RefreshToken refreshToken =
                refreshTokenService
                        .createRefreshToken(user);

        return LoginResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .accountStatus(
                        user.getAccountStatus()
                )
                .accessToken(accessToken)
                .refreshToken(
                        refreshToken.getToken()
                )
                .tokenType("Bearer")
                .expiresIn(
                        jwtService
                                .getAccessTokenExpiration()
                )
                .user(
                        userMapper.toUserResponse(user)
                )
                .build();
    }

    private User findUserForLogin(
            String usernameOrEmail
    ) {

        if (usernameOrEmail.contains("@")) {

            return userRepository
                    .findByEmail(
                            normalizeEmail(
                                    usernameOrEmail
                            )
                    )
                    .orElseThrow(() ->
                            new InvalidCredentialsException(
                                    "Invalid username, email or password."
                            )
                    );
        }

        return userRepository
                .findByUsername(
                        normalizeUsername(
                                usernameOrEmail
                        )
                )
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid username, email or password."
                        )
                );
    }

    /*
     * Logout
     */
    @Override
    @Transactional
    @Auditable(
            action = AuditAction.LOGOUT,
            entityType = AuditEntityType.AUTHENTICATION,
            description = "User logged out of the application",
            captureArguments = false,
            captureResponse = false
    )
    public void logout(
            String refreshToken
    ) {

        refreshTokenService
                .revokeToken(refreshToken);
    }

    /*
     * Email verification
     */
    @Override
    @Transactional
    public void verifyEmail(
            VerifyEmailRequest request
    ) {

        VerificationToken verificationToken =
                verificationService.getByToken(
                        request.getToken()
                );

        if (verificationToken
                .getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new TokenExpiredException(
                    "Verification token has expired."
            );
        }

        if (Boolean.TRUE.equals(
                verificationToken.getUsed()
        )) {

            throw new InvalidTokenException(
                    "Verification token has already been used."
            );
        }

        User user =
                verificationToken.getUser();

        if (Boolean.TRUE.equals(
                user.getEmailVerified()
        )) {

            throw new EmailAlreadyVerifiedException(
                    "Email is already verified."
            );
        }

        user.setEmailVerified(true);

        userRepository.save(user);

        verificationService
                .markAsUsed(verificationToken);
    }

    /*
     * Forgot password
     */
    @Override
    @Transactional
    @Auditable(
            action = AuditAction.PASSWORD_RESET_REQUESTED,
            entityType = AuditEntityType.AUTHENTICATION,
            description = "Password reset was requested",
            captureArguments = false,
            captureResponse = false
    )
    public void forgotPassword(
            ForgotPasswordRequest request
    ) {

        String normalizedEmail =
                normalizeEmail(request.getEmail());

        User user =
                userRepository
                        .findByEmail(normalizedEmail)
                        .orElseThrow(() ->
                                new UserNotFoundException(
                                        "No account was found with this email."
                                )
                        );

        verificationService
                .createPasswordResetToken(user);
    }

    /*
     * Reset password
     */
    @Override
    @Transactional
    @Auditable(
            action = AuditAction.PASSWORD_RESET,
            entityType = AuditEntityType.AUTHENTICATION,
            description = "User password was reset",
            captureArguments = false,
            captureResponse = false
    )
    public void resetPassword(
            ResetPasswordRequest request
    ) {

        if (!request.getNewPassword()
                .equals(
                        request.getConfirmPassword()
                )) {

            throw new PasswordMismatchException(
                    "New password and confirm password do not match."
            );
        }

        PasswordResetToken resetToken =
                verificationService
                        .getPasswordResetToken(
                                request.getResetToken()
                        );

        if (resetToken
                .getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new TokenExpiredException(
                    "Password reset token has expired."
            );
        }

        if (Boolean.TRUE.equals(
                resetToken.getUsed()
        )) {

            throw new InvalidTokenException(
                    "Password reset token has already been used."
            );
        }

        User user =
                resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

        verificationService
                .markPasswordResetTokenAsUsed(
                        resetToken
                );
    }

    private String normalizeEmail(
            String email
    ) {

        return email == null
                ? null
                : email.trim().toLowerCase();
    }

    private String normalizeUsername(
            String username
    ) {

        return username == null
                ? null
                : username.trim();
    }

    private String normalizeRequired(
            String value
    ) {

        return value == null
                ? null
                : value.trim();
    }

    private String normalizeNullable(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}