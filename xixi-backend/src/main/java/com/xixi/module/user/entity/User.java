package com.xixi.module.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String nickname;
    private String role;
    private String roleMotto;
    private String avatarUrl;
    private String phone;
    @TableField("is_admin")
    private Integer isAdmin;
    @TableField("first_login")
    private Integer firstLogin;
    @TableLogic
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public boolean isAdminUser() { return Integer.valueOf(1).equals(isAdmin); }
    public boolean isFirstLogin() { return Integer.valueOf(1).equals(firstLogin); }
}
