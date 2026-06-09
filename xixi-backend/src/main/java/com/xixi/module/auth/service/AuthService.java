package com.xixi.module.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xixi.common.exception.BusinessException;
import com.xixi.common.result.ResultCode;
import com.xixi.module.auth.dto.AuthDto;
import com.xixi.module.user.entity.User;
import com.xixi.module.user.mapper.UserMapper;
import com.xixi.util.CaptchaUtil;
import com.xixi.util.JwtUtil;
import com.xixi.util.RedisKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;
    private final CaptchaUtil captchaUtil;

    public AuthDto.CaptchaResponse generateCaptcha() {
        CaptchaUtil.CaptchaResult result = captchaUtil.generate();
        redisTemplate.opsForValue().set(RedisKeys.captcha(result.captchaId()), result.answer(), 3, TimeUnit.MINUTES);
        AuthDto.CaptchaResponse resp = new AuthDto.CaptchaResponse();
        resp.setCaptchaId(result.captchaId()); resp.setImage(result.base64Image()); return resp;
    }

    public void sendEmailCode(String email) {
        if (redisTemplate.hasKey(RedisKeys.emailLimit(email)))
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS);
        String code = String.format("%06d", new Random().nextInt(1000000));
        redisTemplate.opsForValue().set(RedisKeys.emailCode(email), code, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(RedisKeys.emailLimit(email), "1", 60, TimeUnit.SECONDS);
        sendMail(email, "【汐汐的小窝】注册验证码", "您的注册验证码为：" + code + "，5分钟内有效。请勿泄露给他人 💕");
    }

    public void register(AuthDto.RegisterRequest req) {
        String cachedCode = redisTemplate.opsForValue().get(RedisKeys.emailCode(req.getEmail()));
        if (cachedCode == null || !cachedCode.equals(req.getEmailCode()))
            throw new BusinessException(ResultCode.EMAIL_CODE_ERROR);
        redisTemplate.delete(RedisKeys.emailCode(req.getEmail()));
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername())) > 0)
            throw new BusinessException(ResultCode.CONFLICT.getCode(), "用户名已被注册");
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail, req.getEmail())) > 0)
            throw new BusinessException(ResultCode.CONFLICT.getCode(), "邮箱已被注册");
        User user = new User();
        user.setUsername(req.getUsername()); user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setNickname(req.getUsername()); user.setRole("other");
        user.setRoleMotto("我们都爱汐汐 🌸"); user.setIsAdmin(0); user.setFirstLogin(1); user.setStatus(1);
        userMapper.insert(user);
    }

    public AuthDto.LoginResponse login(AuthDto.LoginRequest req) {
        String cachedAnswer = redisTemplate.opsForValue().get(RedisKeys.captcha(req.getCaptchaId()));
        if (cachedAnswer == null || !cachedAnswer.equals(req.getCaptchaAnswer()))
            throw new BusinessException(ResultCode.CAPTCHA_ERROR);
        redisTemplate.delete(RedisKeys.captcha(req.getCaptchaId()));
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (user == null) throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "用户名或密码错误");
        if (user.getStatus() == 0) throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        String failKey = RedisKeys.loginFail(req.getUsername());
        String failCount = redisTemplate.opsForValue().get(failKey);
        if (failCount != null && Integer.parseInt(failCount) >= 5)
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS.getCode(), "登录失败次数过多，账号已锁內15分钟");
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            redisTemplate.opsForValue().increment(failKey);
            redisTemplate.expire(failKey, 15, TimeUnit.MINUTES);
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "用户名或密码错误");
        }
        redisTemplate.delete(failKey);
        String token = jwtUtil.generateToken(user.getId(), user.isAdminUser());
        redisTemplate.opsForValue().set(RedisKeys.token(user.getId()), token, 7, TimeUnit.DAYS);
        AuthDto.LoginResponse resp = new AuthDto.LoginResponse();
        resp.setToken(token); resp.setUserId(user.getId()); resp.setNickname(user.getNickname());
        resp.setRole(user.getRole()); resp.setRoleMotto(user.getRoleMotto());
        resp.setAvatarUrl(user.getAvatarUrl()); resp.setFirstLogin(user.isFirstLogin()); resp.setIsAdmin(user.isAdminUser());
        return resp;
    }

    public void logout(Long userId) { redisTemplate.delete(RedisKeys.token(userId)); }

    private void sendMail(String to, String subject, String text) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to); msg.setSubject(subject); msg.setText(text);
            mailSender.send(msg);
        } catch (Exception e) { log.error("邮件发送失败: to={}, error={}", to, e.getMessage()); }
    }
}
