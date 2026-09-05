package app.rondondon.beddit.exception;

public class PostException extends BaseException {
    public PostException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    public PostException(ErrorCode errorCode) {super(errorCode);}
}
