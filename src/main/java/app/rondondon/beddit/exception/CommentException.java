package app.rondondon.beddit.exception;

public class CommentException extends BedException {
    public CommentException(ErrorCode errorCode, String message) {super(errorCode, message);}
    public CommentException(ErrorCode errorCode) {super(errorCode);}
}
