package careerpilot_parent.auth.mapper;

import careerpilot_parent.auth.dto.request.RecruiterRegisterRequest;
import careerpilot_parent.auth.dto.response.RegisterResponse;

import careerpilot_parent.user.entity.User;

import org.springframework.stereotype.Component;

@Component
public class RecruiterRegistrationMapper {

    public User toEntity(
            RecruiterRegisterRequest request
    ) {

        if (request == null) {
            return null;
        }

        return User.builder()
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
                        normalizeRequired(
                                request.getUsername()
                        )
                )
                .email(
                        normalizeEmail(
                                request.getEmail()
                        )
                )
                .phoneNumber(
                        normalizeNullable(
                                request.getPhoneNumber()
                        )
                )
                .build();
    }

    public RegisterResponse toRegisterResponse(
            User user
    ) {

        if (user == null) {
            return null;
        }

        return RegisterResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .emailVerificationRequired(true)
                .message(
                        "Recruiter registration successful. " +
                                "Please verify your email before login."
                )
                .build();
    }

    private String normalizeRequired(
            String value
    ) {

        return value == null
                ? null
                : value.trim();
    }

    private String normalizeEmail(
            String email
    ) {

        return email == null
                ? null
                : email.trim().toLowerCase();
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