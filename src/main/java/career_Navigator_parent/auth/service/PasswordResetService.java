package career_Navigator_parent.auth.service;

public interface PasswordResetService {


    void createResetToken(String email);


    void resetPassword(
            String resetToken,
            String newPassword
    );

}