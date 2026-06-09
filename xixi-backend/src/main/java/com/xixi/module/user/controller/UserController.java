package com.xixi.module.user.controller;

import com.xixi.common.result.R;
import com.xixi.module.user.dto.UserDto;
import com.xixi.module.user.service.UserService;
import com.xixi.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/select-role")
    public R<UserDto.SelectRoleResponse> selectRole(@Valid @RequestBody UserDto.SelectRoleRequest req) {
        return R.ok(userService.selectRole(SecurityUtil.getCurrentUserId(), req.getRole()));
    }
    @GetMapping("/profile")
    public R<UserDto.ProfileResponse> getProfile() {
        return R.ok(userService.getProfile(SecurityUtil.getCurrentUserId()));
    }
    @PutMapping("/profile")
    public R<UserDto.ProfileResponse> updateProfile(@Valid @RequestBody UserDto.UpdateProfileRequest req) {
        return R.ok(userService.updateProfile(SecurityUtil.getCurrentUserId(), req));
    }
    @PostMapping("/avatar")
    public R<?> uploadAvatar(@RequestParam("file") MultipartFile file) throws Exception {
        String url = userService.uploadAvatar(SecurityUtil.getCurrentUserId(), file);
        return R.ok(java.util.Map.of("avatarUrl", url));
    }
    @PutMapping("/password")
    public R<Void> changePassword(@Valid @RequestBody UserDto.ChangePasswordRequest req) {
        userService.changePassword(SecurityUtil.getCurrentUserId(), req);
        return R.ok("密码修改成功");
    }
}
