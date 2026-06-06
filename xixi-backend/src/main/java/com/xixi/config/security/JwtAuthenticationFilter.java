package com.xixi.config.security;

import com.xixi.util.JwtUtil;
import com.xixi.util.RedisKeys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final UserDetailsService userDetailsService;
    private static final long RENEW_THRESHOLD_MS = 24 * 60 * 60 * 1000L;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (StringUtils.hasText(token)) {
            try {
                if (jwtUtil.isValid(token)) {
                    Long userId = jwtUtil.getUserId(token);
                    String redisToken = redisTemplate.opsForValue().get(RedisKeys.token(userId));
                    if (token.equals(redisToken)) {
                        Boolean isAdmin = jwtUtil.isAdmin(token);
                        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(String.valueOf(userId));
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        long remaining = jwtUtil.getRemainingExpiry(token);
                        if (remaining < RENEW_THRESHOLD_MS) {
                            String newToken = jwtUtil.generateToken(userId, isAdmin);
                            redisTemplate.opsForValue().set(RedisKeys.token(userId), newToken, 7, TimeUnit.DAYS);
                            response.setHeader("X-Refresh-Token", newToken);
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("Token 解析失败: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
