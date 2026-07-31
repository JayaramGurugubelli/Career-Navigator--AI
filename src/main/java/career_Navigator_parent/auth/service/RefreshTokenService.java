package career_Navigator_parent.auth.service;

import career_Navigator_parent.auth.dto.response.RefreshTokenResponse;
import career_Navigator_parent.auth.entity.RefreshToken;
import career_Navigator_parent.user.entity.User;


public interface RefreshTokenService {


    RefreshToken createRefreshToken(User user);

    RefreshToken verifyExpiration(RefreshToken refreshToken);


    RefreshToken getByToken(String token);


    RefreshTokenResponse refreshAccessToken(String refreshToken);


    void revokeToken(String refreshToken);


    void revokeAllUserTokens(User user);

}