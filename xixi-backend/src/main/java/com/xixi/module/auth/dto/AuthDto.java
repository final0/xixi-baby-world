package com.xixi.module.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

public class AuthDto {
    @Data public static class SendEmailCodeRequest {
        @NotBlank(message = "邮箱不能为空") @Email(message = "邮箱格式不正确") private String email;
    }
    @Data public static class RegisterRequest {
        @NotBlank @Size(min=4,max=20,message="用户名长度为4-20位")
        @Pattern(regexp="^[a-zA-Z0-9_]+$",message="用户名只能包含字母、数字、下划线") private String username;
        @NotBlank @Email(message="邮箱格式不正确") private String email;
        @NotBlank @Size(min=8,max=20,message="密码长度为8-20位")
        @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d).+$",message="密码须包含字母和数字") private String password;
        @NotBlank(message="邮箱验证码不能为空") private String emailCode;
    }
    @Data public static class LoginRequest {
        @NotBlank(message="用户名不能为空") private String username;
        @NotBlank(message="密码不能为空") private String password;
        @NotBlank(message="验证码ID不能为空") private String captchaId;
        @NotBlank(message="验证码答案不能为空") private String captchaAnswer;
    }
    @Data public static class LoginResponse {
        private String token; private Long userId; private String nickname;
        private String role; private String roleMotto; private String avatarUrl;
        private Boolean firstLogin; private Boolean isAdmin;
    }
    @Data public static class CaptchaResponse {
        private String captchaId; private String image;
    }
}
