package careerpilot_parent.auth.service;

import careerpilot_parent.auth.dto.request.ForgotPasswordRequest;
import careerpilot_parent.auth.dto.request.LoginRequest;
import careerpilot_parent.auth.dto.request.RecruiterRegisterRequest;
import careerpilot_parent.auth.dto.request.RegisterRequest;
import careerpilot_parent.auth.dto.request.ResetPasswordRequest;
import careerpilot_parent.auth.dto.request.VerifyEmailRequest;

import careerpilot_parent.auth.dto.response.LoginResponse;
import careerpilot_parent.auth.dto.response.RegisterResponse;

public interface AuthService {

    RegisterResponse register(
            RegisterRequest request
    );

    RegisterResponse registerRecruiter(
            RecruiterRegisterRequest request
    );

    LoginResponse login(
            LoginRequest request
    );

    void logout(
            String refreshToken
    );

    void verifyEmail(
            VerifyEmailRequest request
    );

    void forgotPassword(
            ForgotPasswordRequest request
    );

    void resetPassword(
            ResetPasswordRequest request
    );
}