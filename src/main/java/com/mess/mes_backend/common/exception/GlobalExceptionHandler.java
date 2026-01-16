package com.mess.mes_backend.common.exception;

import com.mess.mes_backend.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        // Print stack trace for debugging purposes
        e.printStackTrace();
        // Return unified JSON error response
        return Result.error(e.getMessage() != null ? e.getMessage() : "Unknown Error");
    }
}
