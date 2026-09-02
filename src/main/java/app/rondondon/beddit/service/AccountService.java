package app.rondondon.beddit.service;

import app.rondondon.beddit.dto.request.ChangeEmailFinishRequest;
import app.rondondon.beddit.dto.request.ChangePasswordRequest;
import app.rondondon.beddit.dto.request.ChangeUsernameRequest;
import app.rondondon.beddit.exception.AuthenticationException;
import app.rondondon.beddit.exception.ErrorCode;
import app.rondondon.beddit.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    @Transactional
    public void changePassword(ChangePasswordRequest req, Long id) {
        userRepository.findById(id).map(user -> {
            if (passwordEncoder.matches(req.oldPassword(), user.getPasswordHash())){
                log.debug("Change from: {} for user: {}", req.oldPassword(), user.getUsername());
                user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
                return user;
            }
            else  {
                log.debug("Incorrect password: {} for user: {}",req.oldPassword(), user.getUsername());
                throw new AuthenticationException(ErrorCode.INCORRECT_PASSWORD);
            }

        })
                .orElseThrow(() -> new AuthenticationException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void changeEmail(ChangeEmailFinishRequest req, Long id) {
        log.debug("Changing email for user with id: {}", id);
        userRepository.findById(id).map(user -> {
                    user.setEmail(req.newEmail());
                    log.debug("Email changed successfully");
                    return user;
                })
                .orElseThrow(() -> {
                    log.debug("Email changing failed(user not found)");
                    return new AuthenticationException(ErrorCode.USER_NOT_FOUND);
                });
    }

    @Transactional
    public void changeUsername(ChangeUsernameRequest req, Long id) {
        log.debug("Changing username for user with id: {}", id);
        userRepository.findById(id).map(user -> {
            if (userRepository.findByUsername(req.newUsername()).isPresent()){
                log.debug("Username change failed(user already exists)");
                throw new AuthenticationException(ErrorCode.USER_ALREADY_EXISTS);
            }
            user.setUsername(req.newUsername());
            return user;
        })
                .orElseThrow(() -> {
                    log.debug("Username change failed(user with given id not found)");
                    return new AuthenticationException(ErrorCode.USER_NOT_FOUND);
                });
    }
}
