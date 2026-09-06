package app.rondondon.beddit.security;

import app.rondondon.beddit.entity.User;
import app.rondondon.beddit.exception.AuthenticationException;
import app.rondondon.beddit.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private final SecretKey jwtSecretKey;
    @Value("${app.jwt.access.exp}")
    private int jwtAccessExp;
    @Value("${app.jwt.refresh.exp}")
    private int jwtRefreshExp;

    JwtService(@Value("${app.jwt.secret}") String secretKey) {
        jwtSecretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateAccessToken(User user) {
        log.trace("Generating access token for user {}", user.getUsername());
        Instant now = Instant.now();
        Instant exp = now.plus(jwtAccessExp, ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("roles", user.getRoles())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("roles", user.getRoles())
                .signWith(jwtSecretKey)
                .compact();
    }

    public String generateRefreshToken(User user) {
        log.trace("Generating refresh token for user {}", user.getUsername());
        Instant now = Instant.now();
        Instant exp = now.plus(jwtRefreshExp, ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .id(UUID.randomUUID().toString())
                .signWith(jwtSecretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            log.trace("Parsing token");
            return Jwts.parser()
                    .verifyWith(jwtSecretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.trace("Failed to parse token(token expired)");
            throw new AuthenticationException(ErrorCode.INCORRECT_TOKEN);
        } catch (SignatureException e) {
            log.trace("Failed to parse token(incorrect token signature)");
            throw new AuthenticationException(ErrorCode.INCORRECT_TOKEN);
        } catch (Exception e) {
            log.trace("Failed to parse token");
            throw new AuthenticationException(ErrorCode.INCORRECT_TOKEN);
        }
    }
}
