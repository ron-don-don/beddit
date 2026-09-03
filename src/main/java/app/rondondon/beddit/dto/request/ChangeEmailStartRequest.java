package app.rondondon.beddit.dto.request;

import app.rondondon.beddit.annotations.CustomEmail;

public record ChangeEmailStartRequest(@CustomEmail String newEmail) {
}
