package com.xixi.module.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AiDto {
    @Data
    public static class ChatRequest {
        @NotBlank(message = "消息不能为空")
        private String message;
        @NotBlank(message = "sessionId 不能为空")
        private String sessionId;
    }
    @Data
    public static class ChatResponse {
        private String reply;
        private String sessionId;
    }
}
