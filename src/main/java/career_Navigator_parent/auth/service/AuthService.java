package career_Navigator_parent.auth.service;

import career_Navigator_parent.auth.dto.request.ForgotPasswordRequest;
import career_Navigator_parent.auth.dto.request.LoginRequest;
import career_Navigator_parent.auth.dto.request.RecruiterRegisterRequest;
import career_Navigator_parent.auth.dto.request.RegisterRequest;
import career_Navigator_parent.auth.dto.request.ResetPasswordRequest;
import career_Navigator_parent.auth.dto.request.VerifyEmailRequest;

import career_Navigator_parent.auth.dto.response.LoginResponse;
import career_Navigator_parent.auth.dto.response.RegisterResponse;

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