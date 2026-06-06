package com.xixi.module.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xixi.common.exception.BusinessException;
import com.xixi.common.result.ResultCode;
import com.xixi.module.ai.dto.AiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ai.api-key}")
    private String apiKey;
    @Value("${ai.base-url}")
    private String baseUrl;
    @Value("${ai.model}")
    private String model;

    private static final String SYSTEM_PROMPT =
        "你是汐汐小助手，是一个专属于汐汐家庭的 AI 助理。\n" +
        "你的性格俏皮可爱、有礼貌、善解人意，非常关心家人的感受。\n" +
        "你喜欢用轻松愉快的语气说话，偶尔加入可爱的表情符号 🌸💕✨。\n" +
        "你的主要职责是帮助记录宝宝成长、回答育儿问题、给家人带来温暖和快乐。\n" +
        "请始终保持积极、温馨的态度。\n" +
        "当用户问及宝宝汐汐的具体信息时，请引导他们去首页查看。";

    private static final int MAX_HISTORY = 20;
    private static final String SESSION_KEY_PREFIX = "ai:session:msg:";

    public AiDto.ChatResponse chat(Long userId, AiDto.ChatRequest req) {
        String sessionKey = SESSION_KEY_PREFIX + req.getSessionId();
        List<String> rawHistory = redisTemplate.opsForList().range(sessionKey, 0, -1);
        List<ObjectNode> messages = new ArrayList<>();

        ObjectNode system = objectMapper.createObjectNode();
        system.put("role", "system");
        system.put("content", SYSTEM_PROMPT);
        messages.add(system);

        if (rawHistory != null) {
            int start = Math.max(0, rawHistory.size() - MAX_HISTORY);
            for (int i = start; i < rawHistory.size(); i++) {
                try { messages.add((ObjectNode) objectMapper.readTree(rawHistory.get(i))); } catch (Exception ignored) {}
            }
        }

        ObjectNode userMsg = objectMapper.createObjectNode();
        userMsg.put("role", "user");
        userMsg.put("content", req.getMessage());
        messages.add(userMsg);

        String reply = callDeepseek(messages);

        try {
            String userMsgJson = objectMapper.writeValueAsString(userMsg);
            ObjectNode assistantMsg = objectMapper.createObjectNode();
            assistantMsg.put("role", "assistant");
            assistantMsg.put("content", reply);
            String assistantMsgJson = objectMapper.writeValueAsString(assistantMsg);
            redisTemplate.opsForList().rightPushAll(sessionKey, userMsgJson, assistantMsgJson);
            redisTemplate.expire(sessionKey, 24, TimeUnit.HOURS);
        } catch (Exception e) { throw new RuntimeException(e); }

        AiDto.ChatResponse resp = new AiDto.ChatResponse();
        resp.setReply(reply);
        resp.setSessionId(req.getSessionId());
        return resp;
    }

    public void clearSession(String sessionId) {
        redisTemplate.delete(SESSION_KEY_PREFIX + sessionId);
    }

    private String callDeepseek(List<ObjectNode> messages) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", model);
            body.put("max_tokens", 1000);
            ArrayNode msgArray = body.putArray("messages");
            messages.forEach(msgArray::add);
            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/v1/chat/completions", entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("Deepseek API 调用失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR);
        }
    }
}
