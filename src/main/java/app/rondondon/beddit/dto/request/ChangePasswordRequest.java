package app.rondondon.beddit.dto.request;

import app.rondondon.beddit.annotations.Password;

public record ChangePasswordRequest(@Password String newPassword, String oldPassword) {
}
