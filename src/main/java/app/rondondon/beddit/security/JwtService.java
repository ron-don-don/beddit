package app.rondondon.beddit.security;

import app.rondondon.beddit.entity.User;
import app.rondondon.beddit.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtService {
    @Value("${app.jwt.access.exp}")
    private int jwtAccessExp;

    @Value("${app.jwt.refresh.exp}")
    private int jwtRefreshExp;

    private final SecretKey jwtSecretKey;

    JwtService (@Value("${app.jwt.secret}") String secretKey) {
        jwtSecretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plus(jwtAccessExp, ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("roles", user.getRoles())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(jwtSecretKey)
                .compact();
    }

    public String generateRefreshToken(User user) {
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
            return Jwts.parser()
                    .verifyWith(jwtSecretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (Exception e) {
            throw new InvalidTokenException("Invalid token");
        }
    }
}
