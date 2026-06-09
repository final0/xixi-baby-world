package com.xixi.module.media.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_video")
public class Video {
    @TableId(type = IdType.AUTO) private Long id;
    private Long uploaderId;
    private String title; private String description;
    private String url; private String coverUrl;
    private Integer duration; private LocalDate takenAt;
    @TableLogic private Integer status;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
}
