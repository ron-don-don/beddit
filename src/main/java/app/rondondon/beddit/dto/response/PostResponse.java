package app.rondondon.beddit.dto.response;

import java.time.Instant;

public record PostResponse(Long id, String title, String text, Long bedId, Long authorId, Instant publishedAt) {
}
