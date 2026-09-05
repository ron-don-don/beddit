package app.rondondon.beddit.dto.response;

import java.time.Instant;

public record BedResponse(Long id, String name, String description, Instant createdAt) {
}
