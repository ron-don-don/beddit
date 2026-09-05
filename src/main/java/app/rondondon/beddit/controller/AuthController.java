package app.rondondon.beddit.controller;

import app.rondondon.beddit.dto.request.AuthRequest;
import app.rondondon.beddit.dto.request.GoogleAuthRequest;
import app.rondondon.beddit.dto.request.JwtLogoutRequest;
import app.rondondon.beddit.dto.request.JwtRefreshRequest;
import app.rondondon.beddit.dto.response.JwtResponse;
import app.rondondon.beddit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody AuthRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody AuthRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }

    @PostMapping("/oauth2/google/login")
    public ResponseEntity<JwtResponse> googleLogin(@RequestBody GoogleAuthRequest req) {
        return ResponseEntity.ok(authService.loginWithGoogle(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody JwtLogoutRequest req) {
        return ResponseEntity.ok(authService.logout(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestBody JwtRefreshRequest req) {
        return ResponseEntity.ok(authService.refresh(req));
    }
}
