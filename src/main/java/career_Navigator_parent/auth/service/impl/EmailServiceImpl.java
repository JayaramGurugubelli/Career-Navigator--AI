package career_Navigator_parent.auth.service.impl;

import career_Navigator_parent.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(
            String to,
            String fullName,
            String verificationLink
    ) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);

        message.setSubject("Verify your CareerPilot account");

        message.setText(
                "Hello " + fullName + ",\n\n"
                        + "Thank you for registering with CareerPilot.\n\n"
                        + "Please click the link below to verify your email:\n\n"
                        + verificationLink
                        + "\n\n"
                        + "This link will expire in 24 hours.\n\n"
                        + "If you did not create this account, you can safely ignore this email.\n\n"
                        + "Regards,\n"
                        + "CareerPilot Team"
        );

        mailSender.send(message);
    }
}