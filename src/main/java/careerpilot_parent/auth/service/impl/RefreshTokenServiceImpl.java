package careerpilot_parent.auth.service.impl;
import careerpilot_parent.audit.annotation.Auditable;
import careerpilot_parent.audit.enums.AuditAction;
import careerpilot_parent.audit.enums.AuditEntityType;
import careerpilot_parent.auth.dto.response.RefreshTokenResponse;
import careerpilot_parent.auth.entity.RefreshToken;
import careerpilot_parent.auth.repository.RefreshTokenRepository;
import careerpilot_parent.auth.service.RefreshTokenService;
import careerpilot_parent.security.jwt.JwtService;
import careerpilot_parent.security.model.CustomUserDetails;
import careerpilot_parent.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    /**
     * Create Refresh Token
     */
    @Override
    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByUser(user)
                .orElse(new RefreshToken());

        refreshToken.setUser(user);

        refreshToken.setToken(
                jwtService.generateRefreshToken(
                        new CustomUserDetails(user)
                )
        );

        refreshToken.setExpiryDate(
                LocalDateTime.now()
                        .plusSeconds(jwtService.getRefreshTokenExpiration() / 1000)
        );

        refreshToken.setRevoked(false);
        refreshToken.setExpired(false);

        return refreshTokenRepository.save(refreshToken);
    }
    /**
     * Validate Refresh Token
     */
    @Override
    public RefreshToken verifyExpiration(
            RefreshToken refreshToken
    ) {


        if(refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {


            refreshToken.setRevoked(true);

            refreshTokenRepository.save(refreshToken);


            throw new RuntimeException(
                    "Refresh token expired"
            );
        }



        if(Boolean.TRUE.equals(
                refreshToken.getRevoked())) {


            throw new RuntimeException(
                    "Refresh token revoked"
            );
        }



        return refreshToken;
    }
    /**
     * Get Refresh Token By Token
     */
    @Override
    public RefreshToken getByToken(String token) {
        return refreshTokenRepository
                .findByToken(token)

                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        "Invalid refresh token"
                                )
                );
    }
    /**
     * Generate New Access Token
     */
    @Override
    @Transactional
    @Auditable(
            action = AuditAction.TOKEN_REFRESHED,
            entityType = AuditEntityType.AUTHENTICATION,
            description = "User access token was refreshed",
            captureArguments = false,
            captureResponse = false
    )
    public RefreshTokenResponse refreshAccessToken(String token) {
        RefreshToken refreshToken = getByToken(token);
        verifyExpiration(refreshToken);
        User user = refreshToken.getUser();
        String accessToken =
                jwtService.generateAccessToken(
                        new CustomUserDetails(user)
                );
        return RefreshTokenResponse.builder()

                .accessToken(accessToken)

                .refreshToken(
                        refreshToken.getToken()
                )

                .tokenType("Bearer")

                .expiresIn(
                        jwtService
                                .getAccessTokenExpiration()
                )

                .build();

    }
    /**
     * Revoke Single Token
     */
    @Override
    public void revokeToken(String token) {
        RefreshToken refreshToken =
                getByToken(token);
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

    }
    /**
     * Logout From All Devices
     */
    @Override
    public void revokeAllUserTokens(User user) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(user);
        tokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);

    }

}