package app.rondondon.beddit.dto.response;

import java.util.List;

public record UserResponse(Long id, String username, List<Long> postsIds) {
}
