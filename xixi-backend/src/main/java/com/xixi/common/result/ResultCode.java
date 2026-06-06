package com.xixi.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或 Token 已失效"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源已存在"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    CAPTCHA_ERROR(1001, "验证码错误或已过期"),
    EMAIL_CODE_ERROR(1002, "邮箱验证码错误或已过期"),
    OLD_PASSWORD_ERROR(1003, "旧密码错误"),
    FILE_TYPE_NOT_ALLOWED(1004, "不支持的文件类型"),
    FILE_SIZE_EXCEEDED(1005, "文件大小超过限制"),
    AI_SERVICE_ERROR(1006, "AI 服务调用失败，请稍后再试"),
    ACCOUNT_DISABLED(1007, "账号已被禁用，请联系管理员");

    private final Integer code;
    private final String message;
    ResultCode(Integer code, String message) { this.code = code; this.message = message; }
}
