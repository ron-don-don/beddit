package app.rondondon.beddit.cache;

import app.rondondon.beddit.security.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class Blacklist {
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtService jwtService;

    private static final Logger log = LoggerFactory.getLogger(Blacklist.class);

    public boolean isRevoked(String id){
        return stringRedisTemplate.hasKey("revoke:" + id);
    }

    public void revoke(String token) {
        log.trace("Revoking token");
        Claims claims = jwtService.parseToken(token);
        var id = claims.getId();
        var ttl = Duration.between(Instant.now(), claims.getExpiration().toInstant());
        if (id == null || ttl.isNegative() || ttl.isZero()) {
            log.warn("Cannot revoke already expired token");
            return;
        }
        log.trace("Token successfully revoked");
        stringRedisTemplate.opsForValue().set("revoke:" + id, "1", ttl);
    }

}
