package app.rondondon.beddit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ErrorCode {

    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_REVOKED(HttpStatus.UNAUTHORIZED),
    UNVERIFIED_EMAIL(HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT),
    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED),
    INCORRECT_TOKEN(HttpStatus.UNAUTHORIZED),
    INCORRECT_GOOGLE_TOKEN(HttpStatus.UNAUTHORIZED),
    GOOGLE_UNAVAILABLE(HttpStatus.INTERNAL_SERVER_ERROR),

    BED_ALREADY_EXISTS(HttpStatus.CONFLICT),
    BED_NOT_FOUND(HttpStatus.UNPROCESSABLE_CONTENT),

    POST_ALREADY_EXISTS(HttpStatus.CONFLICT),
    POST_NOT_FOUND(HttpStatus.UNPROCESSABLE_CONTENT),

    COMMENT_ALREADY_EXISTS(HttpStatus.CONFLICT),
    COMMENT_NOT_FOUND(HttpStatus.UNPROCESSABLE_CONTENT);

    @Getter
    private final HttpStatus httpStatus;

    ErrorCode(HttpStatus httpStatus) {

        this.httpStatus = httpStatus;

    }
}
