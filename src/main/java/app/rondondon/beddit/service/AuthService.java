package app.rondondon.beddit.service;

import app.rondondon.beddit.cache.Blacklist;
import app.rondondon.beddit.dto.request.AuthRequest;
import app.rondondon.beddit.dto.request.GoogleAuthRequest;
import app.rondondon.beddit.dto.request.JwtLogoutRequest;
import app.rondondon.beddit.dto.request.JwtRefreshRequest;
import app.rondondon.beddit.dto.response.JwtResponse;
import app.rondondon.beddit.entity.User;
import app.rondondon.beddit.exception.AuthenticationException;
import app.rondondon.beddit.exception.ErrorCode;
import app.rondondon.beddit.repo.UserRepository;
import app.rondondon.beddit.security.GoogleTokenVerifier;
import app.rondondon.beddit.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final Random random = new Random();
    private final Blacklist blacklist;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Value("${app.jwt.access.exp}")
    private int accessTokenExpireIn;

    public JwtResponse login(AuthRequest req) {
        log.debug("Try login user with username: {}", req.username());
        var user = userRepository.findByUsername(req.username());
        user.map(u -> {
            if (passwordEncoder.matches(req.password(), u.getPasswordHash())){
                log.debug("Login successful");
                return u;
            }
            log.debug("Login failed(password mismatch)");
            throw new AuthenticationException(ErrorCode.INCORRECT_PASSWORD);
        }).orElseThrow(() -> {
            log.debug("Login failed(user not found)");
            return new AuthenticationException(ErrorCode.USER_NOT_FOUND);
        });
        return createJwtResponse(user.get());
    }

    public JwtResponse register(AuthRequest req) {
        log.debug("Try register user with username: {}", req.username());
        var user = userRepository.findByUsername(req.username());
        if (user.isEmpty()) {
            var tempUser = new User();
            tempUser.setUsername(req.username());
            tempUser.setPasswordHash(passwordEncoder.encode(req.password()));
            tempUser.setRoles(List.of("ROLE_USER"));
            var savedUser = userRepository.save(tempUser);
            log.debug("Register successfully");
            return createJwtResponse(savedUser);
        }
        log.debug("Register failed(user already exists)");
        throw new AuthenticationException(ErrorCode.USER_ALREADY_EXISTS);
    }

    public JwtResponse loginWithGoogle(GoogleAuthRequest req) {

        var email = googleTokenVerifier.verify(req.idToken());
        log.debug("Try login with Google with email: {}", email);

        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            log.debug("Login with Google successful, use previous account");
            return createJwtResponse(user.get());
        }

        var username = usernameFromEmail(email);
        var tempUser = new User();
        tempUser.setUsername(username);
        tempUser.setEmail(email);
        var savedUser = userRepository.save(tempUser);
        log.debug("Login with Google successful, created new account");
        return createJwtResponse(savedUser);
    }

    private String usernameFromEmail(String email) {
        var username = email.substring(0, email.indexOf("@"));
        while (userRepository.findByUsername(username).isPresent()) {
            username += random.nextInt(10);
        }
        log.debug("Generated username: {}", username);
        return username;
    }
    @Transactional
    public JwtResponse refresh(JwtRefreshRequest req){
        log.debug("Try refresh token");
        var claims = jwtService.parseToken(req.refresh());
        var id = claims.getId();
        if (blacklist.isRevoked(id)){
            log.warn("Token already revoked");
            throw new AuthenticationException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }
        var sub = Long.decode(claims.getSubject());
        var user = userRepository.findById(sub);
        if (user.isPresent()) {
            log.debug("Refresh successful");
            return createJwtResponse(user.get());
        }
        log.warn("Refresh token has unexpected user id");
        throw new AuthenticationException(ErrorCode.USER_NOT_FOUND);
    }

    public Void logout(JwtLogoutRequest req) {
        blacklist.revoke(req.refresh());
        log.debug("Logout successful");
        return null;
    }

    private JwtResponse createJwtResponse(User user) {
        return new JwtResponse(jwtService.generateAccessToken(user), jwtService.generateRefreshToken(user), accessTokenExpireIn * 60);
    }
}
