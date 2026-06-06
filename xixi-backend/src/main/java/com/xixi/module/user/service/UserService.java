package com.xixi.module.user.service;

import com.xixi.common.exception.BusinessException;
import com.xixi.common.result.ResultCode;
import com.xixi.config.MinioService;
import com.xixi.module.user.dto.UserDto;
import com.xixi.module.user.entity.User;
import com.xixi.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final MinioService minioService;

    private static final Map<String, String> ROLE_MOTTO_MAP = Map.of(
        "dad","爸爸最爱汐汐啦～","mom","妈妈陪着汐汐一起长大 💕",
        "grandpa","爷爷每天都想着汐汐呢～","grandma","奶奶的心肝宝贝！",
        "outerpa","外公最开心的事就是看见汐汐笑","outerma","外婆的汐汐真棒！",
        "other","我们都爱汐汐 🌸");

    public UserDto.SelectRoleResponse selectRole(Long userId, String role) {
        String motto = ROLE_MOTTO_MAP.getOrDefault(role, ROLE_MOTTO_MAP.get("other"));
        User user = new User(); user.setId(userId); user.setRole(role); user.setRoleMotto(motto); user.setFirstLogin(0);
        userMapper.updateById(user);
        UserDto.SelectRoleResponse resp = new UserDto.SelectRoleResponse();
        resp.setRole(role); resp.setRoleMotto(motto); return resp;
    }

    public UserDto.ProfileResponse getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(ResultCode.NOT_FOUND);
        return toProfileResponse(user);
    }

    public UserDto.ProfileResponse updateProfile(Long userId, UserDto.UpdateProfileRequest req) {
        User user = new User(); user.setId(userId);
        if (req.getNickname() != null) user.setNickname(req.getNickname());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        if (req.getRole() != null) { user.setRole(req.getRole()); user.setRoleMotto(ROLE_MOTTO_MAP.getOrDefault(req.getRole(), ROLE_MOTTO_MAP.get("other"))); }
        userMapper.updateById(user); return getProfile(userId);
    }

    public String uploadAvatar(Long userId, MultipartFile file) throws Exception {
        String objectName = "avatars/" + userId + ".jpg";
        minioService.uploadBytes(compressAvatar(file), objectName, "image/jpeg");
        String avatarUrl = minioService.getFileUrl(objectName);
        User user = new User(); user.setId(userId); user.setAvatarUrl(avatarUrl);
        userMapper.updateById(user); return avatarUrl;
    }

    public void changePassword(Long userId, UserDto.ChangePasswordRequest req) {
        User user = userMapper.selectById(userId);
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword()))
            throw new BusinessException(ResultCode.OLD_PASSWORD_ERROR);
        User update = new User(); update.setId(userId);
        update.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userMapper.updateById(update);
    }

    private byte[] compressAvatar(MultipartFile file) throws Exception {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        net.coobird.thumbnailator.Thumbnails.of(file.getInputStream()).size(200, 200).outputFormat("jpg").toOutputStream(baos);
        return baos.toByteArray();
    }

    private UserDto.ProfileResponse toProfileResponse(User user) {
        UserDto.ProfileResponse resp = new UserDto.ProfileResponse();
        resp.setId(user.getId()); resp.setUsername(user.getUsername()); resp.setNickname(user.getNickname());
        resp.setEmail(user.getEmail()); resp.setRole(user.getRole()); resp.setRoleMotto(user.getRoleMotto());
        resp.setAvatarUrl(user.getAvatarUrl()); resp.setPhone(user.getPhone());
        resp.setIsAdmin(user.isAdminUser()); resp.setCreatedAt(user.getCreatedAt());
        return resp;
    }
}
