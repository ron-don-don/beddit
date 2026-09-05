package app.rondondon.beddit.controller;

import app.rondondon.beddit.dto.response.UserResponse;
import app.rondondon.beddit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserInfo(@PathVariable("id") String id, @RequestParam(name = "by") String getBy) throws BadRequestException {
        return switch (getBy) {
            case "username" -> ResponseEntity.ok().body(userService.getInfoByUsername(id));
            case "id" -> ResponseEntity.ok().body(userService.getInfoById(Long.parseLong(id)));
            default -> throw new BadRequestException();
        };
    }
}
