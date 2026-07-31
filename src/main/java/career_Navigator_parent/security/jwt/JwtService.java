package career_Navigator_parent.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import career_Navigator_parent.security.model.CustomUserDetails;

@Service
public class JwtService {


    @Value("${jwt.secret}")
    private String jwtSecret;


    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;


    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;



    /**
     * Generate Access Token
     */
    public String generateAccessToken(
            UserDetails userDetails
    ){

        Map<String,Object> claims =
                new HashMap<>();


        if(userDetails instanceof CustomUserDetails customUserDetails){


            claims.put(
                    "userId",
                    customUserDetails.getUserId()
            );


            claims.put(
                    "roles",
                    customUserDetails.getAuthorities()
                            .stream()
                            .map(Object::toString)
                            .toList()
            );

        }


        return generateToken(
                claims,
                userDetails,
                accessTokenExpiration
        );

    }



    /**
     * Generate Refresh Token
     */
    public String generateRefreshToken(
            UserDetails userDetails
    ){

        return generateToken(
                new HashMap<>(),
                userDetails,
                refreshTokenExpiration
        );
    }



    /**
     * Generate JWT
     */
    private String generateToken(
            Map<String,Object> claims,
            UserDetails userDetails,
            long expiration
    ){

        return Jwts.builder()

                .claims(claims)

                .subject(
                        userDetails.getUsername()
                )

                .issuedAt(
                        new Date()
                )

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration)
                )

                .signWith(
                        getSigningKey(),
                        SignatureAlgorithm.HS256
                )

                .compact();

    }





    public String extractUsername(
            String token
    ){

        return extractClaim(
                token,
                Claims::getSubject
        );

    }





    public Date extractExpiration(
            String token
    ){

        return extractClaim(
                token,
                Claims::getExpiration
        );

    }





    public <T> T extractClaim(
            String token,
            Function<Claims,T> resolver
    ){

        Claims claims =
                extractAllClaims(token);

        return resolver.apply(claims);

    }





    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ){

        String username =
                extractUsername(token);


        return username.equals(
                userDetails.getUsername()
        )
                &&
                !isTokenExpired(token);

    }





    public boolean isTokenExpired(
            String token
    ){

        return extractExpiration(token)
                .before(new Date());

    }





    private Claims extractAllClaims(
            String token
    ){

        return Jwts.parser()

                .verifyWith(
                        getSigningKey()
                )

                .build()

                .parseSignedClaims(token)

                .getPayload();

    }





    private SecretKey getSigningKey(){

        byte[] keyBytes =
                Decoders.BASE64.decode(jwtSecret);


        return Keys.hmacShaKeyFor(
                keyBytes
        );

    }





    public long getAccessTokenExpiration(){

        return accessTokenExpiration;

    }





    public long getRefreshTokenExpiration(){

        return refreshTokenExpiration;

    }

}