package careerpilot_parent.auth.controller;

import careerpilot_parent.auth.dto.request.ForgotPasswordRequest;
import careerpilot_parent.auth.dto.request.LoginRequest;
import careerpilot_parent.auth.dto.request.LogoutRequest;
import careerpilot_parent.auth.dto.request.RecruiterRegisterRequest;
import careerpilot_parent.auth.dto.request.RefreshTokenRequest;
import careerpilot_parent.auth.dto.request.RegisterRequest;
import careerpilot_parent.auth.dto.request.ResendVerificationRequest;
import careerpilot_parent.auth.dto.request.ResetPasswordRequest;
import careerpilot_parent.auth.dto.request.VerifyEmailRequest;

import careerpilot_parent.auth.dto.response.LoginResponse;
import careerpilot_parent.auth.dto.response.RefreshTokenResponse;
import careerpilot_parent.auth.dto.response.RegisterResponse;

import careerpilot_parent.auth.service.AuthService;
import careerpilot_parent.auth.service.RefreshTokenService;
import careerpilot_parent.auth.service.VerificationService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final VerificationService verificationService;

    private final RefreshTokenService refreshTokenService;

    /*
     * Student registration
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid
            @RequestBody
            RegisterRequest request
    ) {

        RegisterResponse response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /*
     * Recruiter registration
     */
    @PostMapping("/recruiter/register")
    public ResponseEntity<RegisterResponse> registerRecruiter(
            @Valid
            @RequestBody
            RecruiterRegisterRequest request
    ) {

        RegisterResponse response =
                authService.registerRecruiter(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /*
     * Login for all users:
     * STUDENT, RECRUITER and ADMIN
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid
            @RequestBody
            LoginRequest request
    ) {

        LoginResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }

    /*
     * Logout using refresh token
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @Valid
            @RequestBody
            LogoutRequest request
    ) {

        authService.logout(
                request.getRefreshToken()
        );

        return ResponseEntity.ok(
                "Logout successful"
        );
    }

    /*
     * Verify email by JSON request body.
     */
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(
            @Valid
            @RequestBody
            VerifyEmailRequest request
    ) {

        verificationService.verifyEmail(
                request.getToken()
        );

        return ResponseEntity.ok(
                "Email verified successfully"
        );
    }

    /*
     * Verify email by link sent to email.
     *
     * Example:
     * GET /api/auth/verify-email-link?token=abc
     */
    @GetMapping("/verify-email-link")
    public ResponseEntity<String> verifyEmailFromLink(
            @RequestParam
            String token
    ) {

        verificationService.verifyEmail(token);

        return ResponseEntity.ok(
                "Email verified successfully"
        );
    }

    /*
     * Resend verification email
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(
            @Valid
            @RequestBody
            ResendVerificationRequest request
    ) {

        verificationService.resendVerificationToken(
                request.getEmail()
        );

        return ResponseEntity.ok(
                "Verification email sent successfully"
        );
    }

    /*
     * Refresh access token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @Valid
            @RequestBody
            RefreshTokenRequest request
    ) {

        RefreshTokenResponse response =
                refreshTokenService.refreshAccessToken(
                        request.getRefreshToken()
                );

        return ResponseEntity.ok(response);
    }

    /*
     * Send password-reset email
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid
            @RequestBody
            ForgotPasswordRequest request
    ) {

        authService.forgotPassword(request);

        return ResponseEntity.ok(
                "Password reset link sent successfully"
        );
    }

    /*
     * Reset password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid
            @RequestBody
            ResetPasswordRequest request
    ) {

        authService.resetPassword(request);

        return ResponseEntity.ok(
                "Password reset successfully"
        );
    }
}