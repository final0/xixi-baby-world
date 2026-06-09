package com.xixi.module.diary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DiaryDto {
    @Data public static class CreateRequest {
        @NotBlank(message="标题不能为空") private String title;
        @NotBlank(message="内容不能为空") private String content;
        @NotNull(message="日记日期不能为空") private LocalDate diaryDate;
        private String mood;
    }
    @Data public static class UpdateRequest {
        private String title; private String content;
        private LocalDate diaryDate; private String mood;
    }
    @Data public static class DiaryResponse {
        private Long id; private String title; private String content;
        private LocalDate diaryDate; private String mood;
        private Long authorId; private String authorNickname; private String authorAvatarUrl;
        private LocalDateTime createdAt; private LocalDateTime updatedAt;
    }
}
