package com.factor.interfaces.rest;

import com.factor.common.api.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Dify 智能聊天助手代理
 * 前端 → 后端（隐藏 API-Key）→ Dify Service API
 */
@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    @Value("${dify.api-url:http://localhost/v1}")
    private String difyBaseUrl;

    @Value("${dify.api-key:app-xxxxx}")
    private String difyApiKey;

    private final RestTemplate rest = new RestTemplate();

    /** 发送聊天消息（阻塞模式，适合侧边栏一次返回） */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, Object> body) {
        String url = difyBaseUrl + "/chat-messages";

        // 组装 Dify 请求体
        Map<String, Object> req = new LinkedHashMap<>();
        req.put("query", body.getOrDefault("query", ""));
        req.put("inputs", body.getOrDefault("inputs", Map.of()));
        req.put("response_mode", "blocking");
        req.put("user", body.getOrDefault("user", "frontend-user"));
        req.put("conversation_id", body.getOrDefault("conversation_id", ""));
        req.put("auto_generate_name", false);

        // 发送请求到 Dify
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(difyApiKey);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(req, headers);

        try {
            ResponseEntity<Map> resp = rest.postForEntity(url, entity, Map.class);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", true);
            result.put("data", resp.getBody());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("error", e.getMessage());
            return ResponseEntity.status(500).body(err);
        }
    }

    /** 获取会话列表 */
    @GetMapping("/conversations")
    public ResponseEntity<Map<String, Object>> conversations(@RequestParam(defaultValue = "frontend-user") String user,
                                                              @RequestParam(defaultValue = "20") int limit) {
        String url = difyBaseUrl + "/conversations?user=" + user + "&limit=" + limit;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(difyApiKey);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> resp = rest.exchange(url, HttpMethod.GET, entity, Map.class);
            return ResponseEntity.ok(Map.of("success", true, "data", resp.getBody()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** 删除会话 */
    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<Map<String, Object>> deleteConversation(@PathVariable String id,
                                                                    @RequestParam(defaultValue = "frontend-user") String user) {
        String url = difyBaseUrl + "/conversations/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(difyApiKey);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(Map.of("user", user), headers);

        try {
            rest.exchange(url, HttpMethod.DELETE, entity, Map.class);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
