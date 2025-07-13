package com.github.ludomikula.sires;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> handeException(Exception cause) {
        return ResponseEntity.internalServerError().body(
                new ErrorResult(
                        false,
                        cause.getMessage(),
                        ExceptionUtils.getStackTrace(cause)
                )
        );
    }

    public record ErrorResult(boolean success, String message, String stacktrace) {}
}
