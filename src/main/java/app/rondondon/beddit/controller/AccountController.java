package app.rondondon.beddit.controller;

import app.rondondon.beddit.dto.request.ChangeEmailFinishRequest;
import app.rondondon.beddit.dto.request.ChangeEmailStartRequest;
import app.rondondon.beddit.dto.request.ChangePasswordRequest;
import app.rondondon.beddit.dto.request.ChangeUsernameRequest;
import app.rondondon.beddit.dto.response.EmailVerificationResponse;
import app.rondondon.beddit.service.AccountService;
import app.rondondon.beddit.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final EmailService emailService;

    @PutMapping("/change/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req, @AuthenticationPrincipal Long id) {
        accountService.changePassword(req, id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change/email/start")
    public ResponseEntity<Void> changeEmailStart(@Valid @RequestBody ChangeEmailStartRequest req, @AuthenticationPrincipal Long id) {
        emailService.sendVerificationCode(req.newEmail(), id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change/email/finish")
    public ResponseEntity<EmailVerificationResponse> changeEmailFinish(@Valid @RequestBody ChangeEmailFinishRequest req, @AuthenticationPrincipal Long id) {
        var verified = emailService.verifyVerificationCode(req.code(), id, req.newEmail());
         if (verified.verified()){
             accountService.changeEmail(req, id);
         }
        return ResponseEntity.ok().body(verified);
    }

    @PutMapping("/change/username")
    public ResponseEntity<Void> changeUsername(@Valid @RequestBody ChangeUsernameRequest req, @AuthenticationPrincipal Long id) {
        accountService.changeUsername(req, id);
        return ResponseEntity.ok().build();
    }
}
