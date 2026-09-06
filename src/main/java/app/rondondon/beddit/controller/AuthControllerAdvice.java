package app.rondondon.beddit.controller;

import app.rondondon.beddit.exception.AuthenticationException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthControllerAdvice {

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleInvalidTokenException(AuthenticationException e) {
        var detail = ProblemDetail.forStatus(e.getErrorCode().getHttpStatus());
        detail.setDetail("Expected error occurred");
        detail.setProperty("error_code", e.getErrorCode());
        return detail;
    }
}
