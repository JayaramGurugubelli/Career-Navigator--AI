package career_Navigator_parent.auth.service;

public interface EmailService {

    void sendVerificationEmail(
            String to,
            String fullName,
            String verificationLink
    );

}