package app.rondondon.beddit.exception;

public class InvalidGoogleTokenException extends RuntimeException {
    public InvalidGoogleTokenException(String message) {
        super(message);
    }
}
