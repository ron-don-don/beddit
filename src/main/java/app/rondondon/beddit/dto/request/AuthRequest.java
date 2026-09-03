package app.rondondon.beddit.dto.request;

import app.rondondon.beddit.annotations.Password;
import app.rondondon.beddit.annotations.Username;

public record AuthRequest(@Username String username, @Password String password) {
}
