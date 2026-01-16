package com.mess.mes_backend.common.exception;

import com.mess.mes_backend.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        // 打印堆栈跟踪以进行调试
        e.printStackTrace();
        // 返回统一的 JSON 错误响应
        return Result.error(e.getMessage() != null ? e.getMessage() : "未知错误");
    }
}
