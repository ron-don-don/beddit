package app.rondondon.beddit.exception;

public class UserException extends BaseException {
    public UserException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    public UserException(ErrorCode errorCode) { super(errorCode); }
}
