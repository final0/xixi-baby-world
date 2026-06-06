package com.xixi.module.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xixi.common.exception.BusinessException;
import com.xixi.module.admin.entity.SystemConfig;
import com.xixi.module.admin.mapper.SystemConfigMapper;
import com.xixi.module.baby.entity.BabyInfo;
import com.xixi.module.baby.mapper.BabyInfoMapper;
import com.xixi.module.media.dto.MediaDto;
import com.xixi.module.motto.entity.Motto;
import com.xixi.module.motto.mapper.MottoMapper;
import com.xixi.module.user.entity.User;
import com.xixi.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final BabyInfoMapper babyInfoMapper;
    private final SystemConfigMapper systemConfigMapper;
    private final UserMapper userMapper;
    private final MottoMapper mottoMapper;

    public void updateBabyInfo(Map<String, Object> body) {
        BabyInfo info = babyInfoMapper.selectById(1L);
        if (info == null) { info = new BabyInfo(); info.setId(1L); info.setName("汐汐"); }
        if (body.get("birthday") != null) info.setBirthday(LocalDate.parse((String) body.get("birthday")));
        if (body.get("birthWeight") != null) info.setBirthWeight(new BigDecimal(body.get("birthWeight").toString()));
        if (body.get("birthHeight") != null) info.setBirthHeight(new BigDecimal(body.get("birthHeight").toString()));
        if (body.get("intro") != null) info.setIntro((String) body.get("intro"));
        if (body.get("name") != null) info.setName((String) body.get("name"));
        if (babyInfoMapper.selectById(1L) == null) { babyInfoMapper.insert(info); } else { babyInfoMapper.updateById(info); }
    }

    public Map<String, String> getConfigs(List<String> keys) {
        Map<String, String> result = new HashMap<>();
        if (keys == null || keys.isEmpty()) {
            systemConfigMapper.selectList(null).forEach(c -> result.put(c.getConfigKey(), c.getConfigValue()));
        } else {
            systemConfigMapper.selectList(new LambdaQueryWrapper<SystemConfig>().in(SystemConfig::getConfigKey, keys))
                    .forEach(c -> result.put(c.getConfigKey(), c.getConfigValue()));
        }
        return result;
    }

    public void updateConfig(String key, String value) {
        SystemConfig existing = systemConfigMapper.selectOne(new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, key));
        if (existing == null) {
            SystemConfig config = new SystemConfig(); config.setConfigKey(key); config.setConfigValue(value);
            systemConfigMapper.insert(config);
        } else { existing.setConfigValue(value); systemConfigMapper.updateById(existing); }
    }

    public void testEmail(String toEmail) {
        Map<String, String> configs = getConfigs(List.of("smtp_host","smtp_port","smtp_username","smtp_password","smtp_ssl"));
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(configs.getOrDefault("smtp_host", "smtp.qq.com"));
        sender.setPort(Integer.parseInt(configs.getOrDefault("smtp_port", "465")));
        sender.setUsername(configs.get("smtp_username"));
        sender.setPassword(configs.get("smtp_password"));
        sender.setProtocol("smtps");
        Properties props = sender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(configs.get("smtp_username")); msg.setTo(toEmail);
            msg.setSubject("【汐汐的小窝】邮件测试"); msg.setText("邮件发送测试成功！汐汐的小窝管理员配置正常 🌸");
            sender.send(msg);
        } catch (Exception e) { throw new BusinessException("邮件发送失败: " + e.getMessage()); }
    }

    public MediaDto.PageResult<Map<String, Object>> listUsers(int page, int size) {
        Page<User> pageParam = new Page<>(page, size);
        userMapper.selectPage(pageParam, new LambdaQueryWrapper<User>().orderByDesc(User::getCreatedAt));
        MediaDto.PageResult<Map<String, Object>> result = new MediaDto.PageResult<>();
        result.setTotal(pageParam.getTotal()); result.setPages((int) pageParam.getPages());
        result.setList(pageParam.getRecords().stream().map(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId()); m.put("username", u.getUsername()); m.put("nickname", u.getNickname());
            m.put("email", u.getEmail()); m.put("role", u.getRole()); m.put("isAdmin", u.getIsAdmin());
            m.put("status", u.getStatus()); m.put("createdAt", u.getCreatedAt());
            return m;
        }).toList());
        return result;
    }

    public void updateUserStatus(Long userId, Integer status) {
        User user = new User(); user.setId(userId); user.setStatus(status); userMapper.updateById(user);
    }

    public List<Motto> listMottos() {
        return mottoMapper.selectList(new LambdaQueryWrapper<Motto>().orderByDesc(Motto::getCreatedAt));
    }

    public Motto addMotto(String content, String author) {
        Motto motto = new Motto(); motto.setContent(content); motto.setAuthor(author); motto.setIsActive(1);
        mottoMapper.insert(motto); return motto;
    }

    public void deleteMotto(Long id) { mottoMapper.deleteById(id); }

    public void updateMottoStatus(Long id, Integer isActive) {
        Motto motto = new Motto(); motto.setId(id); motto.setIsActive(isActive); mottoMapper.updateById(motto);
    }
}
