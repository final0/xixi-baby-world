package com.xixi.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> implements Serializable {
    private Integer code;
    private String message;
    private T data;
    private R() {}
    public static <T> R<T> ok() {
        R<T> r = new R<>();
        r.code = ResultCode.SUCCESS.getCode();
        r.message = ResultCode.SUCCESS.getMessage();
        return r;
    }
    public static <T> R<T> ok(T data) { R<T> r = ok(); r.data = data; return r; }
    public static <T> R<T> ok(String message, T data) { R<T> r = ok(); r.message = message; r.data = data; return r; }
    public static <T> R<T> fail(ResultCode resultCode) {
        R<T> r = new R<>();
        r.code = resultCode.getCode();
        r.message = resultCode.getMessage();
        return r;
    }
    public static <T> R<T> fail(Integer code, String message) {
        R<T> r = new R<>();
        r.code = code;
        r.message = message;
        return r;
    }
    public static <T> R<T> fail(String message) { return fail(ResultCode.INTERNAL_ERROR.getCode(), message); }
}
