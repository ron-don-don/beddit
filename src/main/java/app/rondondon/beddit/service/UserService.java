package app.rondondon.beddit.service;

import app.rondondon.beddit.dto.response.UserResponse;
import app.rondondon.beddit.entity.Post;
import app.rondondon.beddit.exception.ErrorCode;
import app.rondondon.beddit.exception.UserException;
import app.rondondon.beddit.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserResponse getInfoById(Long userId){
        var u =  userRepository.findById(userId).orElseThrow(() -> {
            log.debug("No user found with id {}", userId);
            return new UserException(ErrorCode.USER_NOT_FOUND);
        });
        return new UserResponse(u.getId(), u.getUsername(), u.getPosts().stream().map(Post::getId).toList());
    }
    public UserResponse getInfoByUsername(String username){
        var u =  userRepository.findByUsername(username).orElseThrow(() -> {
            log.debug("No user found with username {}", username);
            return new UserException(ErrorCode.USER_NOT_FOUND);
        });
        return new UserResponse(u.getId(), u.getUsername(), u.getPosts().stream().map(Post::getId).toList());
    }
}
