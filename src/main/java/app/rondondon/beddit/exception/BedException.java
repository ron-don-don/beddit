package app.rondondon.beddit.exception;

public class BedException extends BaseException {
    public BedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    public BedException(ErrorCode errorCode) {super(errorCode);}
}
