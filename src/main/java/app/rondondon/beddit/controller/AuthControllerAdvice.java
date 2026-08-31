package app.rondondon.beddit.controller;

import app.rondondon.beddit.exception.InvalidCredentialsException;
import app.rondondon.beddit.exception.InvalidGoogleTokenException;
import app.rondondon.beddit.exception.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthControllerAdvice {
    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentialsException(InvalidCredentialsException e) {
        var detail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        detail.setDetail(e.getMessage());
        return detail;
    }

    @ExceptionHandler(InvalidGoogleTokenException.class)
    public ProblemDetail handleInvalidGoogleTokenException(InvalidGoogleTokenException e) {
        var detail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        detail.setDetail(e.getMessage());
        return detail;
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ProblemDetail handleInvalidTokenException(InvalidTokenException e) {
        var detail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        detail.setDetail(e.getMessage());
        return detail;
    }
}
