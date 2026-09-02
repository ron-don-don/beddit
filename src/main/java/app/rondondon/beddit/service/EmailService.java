package app.rondondon.beddit.service;

import app.rondondon.beddit.dto.response.EmailVerificationResponse;
import app.rondondon.beddit.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.SetCondition;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private final Random random = new Random();

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.mail.message.ttl}")
    private int ttl;

    @Value("${app.mail.message.attempts}")
    private int attempts;

    @Async
    public void sendVerificationCode(String email, Long userId) {
        log.debug("Sending verification code for email: {}", email);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("This is your verification code; do not share it with anyone.");

        var code = createVerificationCode(email, userId);
        message.setText(code);

        mailSender.send(message);
        log.debug("Sent verification code for email: {}", email);
    }
    private String createVerificationCode(String email, Long userId) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++){
            code.append(random.nextInt(9));
        }
        log.trace("Created verification code for email, redis record: {}", "verification:" + code + ":" + userId + ":" + email);
        redisTemplate.opsForValue().set("verification:" + code + ":" + userId + ":" + email, String.valueOf(attempts), Duration.ofMinutes(ttl));
        return code.toString();
    }
    public EmailVerificationResponse verifyVerificationCode(String code, Long userId, String email) {
        var stringAttempts = redisTemplate.opsForValue().get("verification:" + code + ":" + userId + ":" + email);
        if (stringAttempts == null) {
            log.warn("Could not find remaining attempts for verification code, redis record: {}", "verification:" + code + ":" + userId + ":" + email);
            return new EmailVerificationResponse(false);
        }
        final var attemptsRemain = Integer.parseInt(stringAttempts);
        if  (attemptsRemain <= 0) {
            redisTemplate.delete("verification:" + code + ":" + userId  + ":" + email);
            log.trace("No more attempts for verify for verification code, deleted redis record: {}", "verification:" + code + ":" + userId + ":" + email);
            return new EmailVerificationResponse(false);
        }
        log.trace("Reduce attempts, attempts remain: {}", attemptsRemain - 1);
        redisTemplate.execute((RedisCallback<Boolean>) connection ->
                connection.stringCommands().set(
                        ("verification:" + code + ":" + userId + ":" + email).getBytes(),
                        String.valueOf(attemptsRemain - 1).getBytes(),
                        SetCondition.upsert(),
                        Expiration.keepTtl()
                )
        );
        log.debug("Email successfully verified");
        return new EmailVerificationResponse(true);
    }
}
