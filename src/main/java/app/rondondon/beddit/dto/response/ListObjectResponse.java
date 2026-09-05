package app.rondondon.beddit.dto.response;

import java.util.List;

public record ListObjectResponse<T>(List<T> objects, Boolean hasNext) {
}
