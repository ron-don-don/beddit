package app.rondondon.beddit.exception;

public class AuthenticationException extends BaseException {
    public AuthenticationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    public AuthenticationException(ErrorCode errorCode) {super(errorCode);}
}
