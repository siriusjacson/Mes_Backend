package com.mess.mes_backend.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code; // 200: Success, 500: Error
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage("Success");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMessage(msg);
        return r;
    }
}
