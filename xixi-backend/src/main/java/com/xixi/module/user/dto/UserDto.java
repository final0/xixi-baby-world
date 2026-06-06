package com.xixi.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

public class UserDto {
    @Data public static class SelectRoleRequest {
        @NotBlank(message = "角色不能为空") private String role;
    }
    @Data public static class UpdateProfileRequest {
        @Size(max = 50, message = "昵称最长50字符") private String nickname;
        private String phone;
        private String role;
    }
    @Data public static class ChangePasswordRequest {
        @NotBlank(message = "旧密码不能为空") private String oldPassword;
        @NotBlank(message = "新密码不能为空")
        @Size(min = 8, max = 20, message = "密码长度为8-20位")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "密码须包含字母和数字")
        private String newPassword;
    }
    @Data public static class ProfileResponse {
        private Long id; private String username; private String nickname;
        private String email; private String role; private String roleMotto;
        private String avatarUrl; private String phone; private Boolean isAdmin;
        private LocalDateTime createdAt;
    }
    @Data public static class SelectRoleResponse {
        private String role; private String roleMotto;
    }
}
