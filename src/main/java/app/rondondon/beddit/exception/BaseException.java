package app.rondondon.beddit.exception;

import lombok.Getter;

public class BaseException extends RuntimeException {
    @Getter
    private final ErrorCode errorCode;
    public BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    public BaseException(ErrorCode errorCode) {
        super("error containment: " + errorCode.toString());
        this.errorCode = errorCode;
    }
}
