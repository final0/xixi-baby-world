package com.xixi.module.media.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MediaDto {
    @Data public static class PhotoResponse {
        private Long id; private String url; private String thumbnailUrl;
        private String title; private String description; private LocalDate takenAt;
        private String uploaderNickname; private Long uploaderId;
        private Integer isFeatured; private LocalDateTime createdAt;
    }
    @Data public static class VideoResponse {
        private Long id; private String title; private String description;
        private String coverUrl; private Integer duration; private LocalDate takenAt;
        private String uploaderNickname; private Long uploaderId; private LocalDateTime createdAt;
    }
    @Data public static class PageResult<T> {
        private Long total; private Integer pages; private List<T> list;
    }
}
