package com.xixi.module.motto.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_motto")
public class Motto {
    @TableId(type = IdType.AUTO) private Long id;
    private String content;
    private String author;
    private Integer isActive;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
}
