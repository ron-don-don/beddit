package app.rondondon.beddit.dto.request;

import app.rondondon.beddit.annotations.Username;

public record ChangeUsernameRequest(@Username String newUsername) {
}
