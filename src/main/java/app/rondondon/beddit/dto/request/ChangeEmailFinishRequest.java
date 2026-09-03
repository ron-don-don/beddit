package app.rondondon.beddit.dto.request;

import app.rondondon.beddit.annotations.CustomEmail;

public record ChangeEmailFinishRequest(String code, @CustomEmail String newEmail) {
}
