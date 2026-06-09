package com.xixi.module.diary.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_diary")
public class Diary {
    @TableId(type = IdType.AUTO) private Long id;
    private Long authorId; private String title; private String content;
    private LocalDate diaryDate; private String mood;
    @TableLogic private Integer status;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
}
