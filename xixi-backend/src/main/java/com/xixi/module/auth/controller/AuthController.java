package com.xixi.module.auth.controller;

import com.xixi.common.result.R;
import com.xixi.module.auth.dto.AuthDto;
import com.xixi.module.auth.service.AuthService;
import com.xixi.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/captcha")
    public R<AuthDto.CaptchaResponse> getCaptcha() { return R.ok(authService.generateCaptcha()); }

    @PostMapping("/send-email-code")
    public R<Void> sendEmailCode(@Valid @RequestBody AuthDto.SendEmailCodeRequest req) {
        authService.sendEmailCode(req.getEmail());
        return R.ok("验证码已发送，请查收邮件 💌");
    }

    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody AuthDto.RegisterRequest req) {
        authService.register(req);
        return R.ok("注册成功，欢迎加入汐汐的小窝 🌸");
    }

    @PostMapping("/login")
    public R<AuthDto.LoginResponse> login(@Valid @RequestBody AuthDto.LoginRequest req) {
        return R.ok(authService.login(req));
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId != null) authService.logout(userId);
        return R.ok("已安全退出 👋");
    }
}
