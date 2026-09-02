package app.rondondon.beddit.dto.request;

public record ChangePasswordRequest(String newPassword, String oldPassword) {
}
