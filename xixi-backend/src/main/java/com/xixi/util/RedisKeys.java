package com.xixi.util;

public class RedisKeys {
    public static final String CAPTCHA = "captcha:";
    public static final String EMAIL_CODE = "email:code:";
    public static final String EMAIL_LIMIT = "email:limit:";
    public static final String TOKEN = "token:";
    public static final String LOGIN_FAIL = "login:fail:";
    public static final String AI_SESSION = "ai:session:";

    public static String captcha(String captchaId) { return CAPTCHA + captchaId; }
    public static String emailCode(String email) { return EMAIL_CODE + email; }
    public static String emailLimit(String email) { return EMAIL_LIMIT + email; }
    public static String token(Long userId) { return TOKEN + userId; }
    public static String loginFail(String username) { return LOGIN_FAIL + username; }
    public static String aiSession(String sessionId) { return AI_SESSION + sessionId; }
}
