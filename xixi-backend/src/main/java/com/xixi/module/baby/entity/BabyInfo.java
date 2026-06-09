package com.xixi.module.baby.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_baby_info")
public class BabyInfo {
    @TableId(type = IdType.AUTO) private Long id;
    private String name;
    private LocalDate birthday;
    private BigDecimal birthWeight;
    private BigDecimal birthHeight;
    private String avatarUrl;
    private String intro;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
}
