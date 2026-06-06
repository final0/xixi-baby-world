package com.xixi.module.ai.controller;

import com.xixi.common.result.R;
import com.xixi.module.ai.dto.AiDto;
import com.xixi.module.ai.service.AiService;
import com.xixi.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiService aiService;

    @PostMapping("/chat")
    public R<AiDto.ChatResponse> chat(@Valid @RequestBody AiDto.ChatRequest req) {
        return R.ok(aiService.chat(SecurityUtil.getCurrentUserId(), req));
    }

    @DeleteMapping("/session/{sessionId}")
    public R<Void> clearSession(@PathVariable String sessionId) {
        aiService.clearSession(sessionId);
        return R.ok("会话已清除");
    }
}
