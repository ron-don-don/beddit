package app.rondondon.beddit.service;

import app.rondondon.beddit.cache.Blacklist;
import app.rondondon.beddit.dto.request.AuthRequest;
import app.rondondon.beddit.dto.request.GoogleAuthRequest;
import app.rondondon.beddit.dto.request.JwtLogoutRequest;
import app.rondondon.beddit.dto.request.JwtRefreshRequest;
import app.rondondon.beddit.dto.response.JwtResponse;
import app.rondondon.beddit.entity.User;
import app.rondondon.beddit.exception.InvalidCredentialsException;
import app.rondondon.beddit.exception.InvalidTokenException;
import app.rondondon.beddit.repo.UserRepository;
import app.rondondon.beddit.security.GoogleTokenVerifier;
import app.rondondon.beddit.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Value("${app.jwt.access.exp}")
    private int accessTokenExpireIn;

    public JwtResponse login(AuthRequest req) {
        var user = userRepository.findByUsername(req.username());
        if (user.isPresent() &&  passwordEncoder.matches(req.password(), user.get().getPasswordHash())) {
            return createJwtResponse(user.get());
        }
        throw new InvalidCredentialsException("Invalid username or password");
    }

    public JwtResponse register(AuthRequest req) {
        var user = userRepository.findByUsername(req.username());
        if (user.isEmpty()) {
            var tempUser = new User();
            tempUser.setUsername(req.username());
            tempUser.setPasswordHash(passwordEncoder.encode(req.password()));
            var savedUser = userRepository.save(tempUser);
            return createJwtResponse(savedUser);
        }
        throw new InvalidCredentialsException("User already exists");
    }

    public JwtResponse loginWithGoogle(GoogleAuthRequest req) {
        var email = googleTokenVerifier.verify(req.idToken());

        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return createJwtResponse(user.get());
        }

        var username = usernameFromEmail(email);
        var tempUser = new User();
        tempUser.setUsername(username);
        tempUser.setEmail(email);
        var savedUser = userRepository.save(tempUser);
        return createJwtResponse(savedUser);
    }

    private String usernameFromEmail(String email) {
        var username = email.substring(0, email.indexOf("@"));
        while (userRepository.findByUsername(username).isPresent()) {
            username += random.nextInt(10);
        }
        return username;
    }
    @Transactional
    public JwtResponse refresh(JwtRefreshRequest req){
        var claims = jwtService.parseToken(req.refresh());
        var id = claims.getId();
        if (blacklist.isRevoked(id)){
            throw new InvalidTokenException("Refresh token already revoked");
        }
        var sub = Long.decode(claims.getSubject());
        var user = userRepository.findById(sub);
        if (user.isPresent()) {
            logout(new JwtLogoutRequest(req.refresh()));
            return  createJwtResponse(user.get());
        }
        throw new InvalidTokenException("Refresh token has unexpected user id");
    }

    public Void logout(JwtLogoutRequest req) {
        blacklist.revoke(req.refresh());
        return null;
    }

    private JwtResponse createJwtResponse(User user) {
        return new JwtResponse(jwtService.generateAccessToken(user), jwtService.generateRefreshToken(user), accessTokenExpireIn * 60);
    }
}
