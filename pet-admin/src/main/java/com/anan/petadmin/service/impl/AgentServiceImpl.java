package com.anan.petadmin.service.impl;

import com.anan.petadmin.dto.AgentDTO;
import com.anan.petadmin.service.AgentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AgentServiceImpl implements AgentService {
    @Resource
    private RestTemplate restTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public String get(AgentDTO agentDTO) {
         String fastApiUrl = "http://localhost:8000/agent/stream";
//        String fastApiUrl = "http://agent:8000/agent/stream";
        try {
            String url = fastApiUrl + "?query=" + agentDTO.getQuery() +
                    "&session_id=" + agentDTO.getSessionId() +
                    "&user_id=" + agentDTO.getUserId() ;
            String result = restTemplate.getForObject(url, String.class);
            log.info("FastAPI 响应结果: {}", result);
             // 解析 JSON，提取内容
            String content = result;
            try {
                JsonNode rootNode = objectMapper.readTree(result);
                
                if (rootNode.isArray() && rootNode.size() > 0) {
                    // 如果是数组，取第一个元素
                    content = rootNode.get(0).asText();
                } else if (rootNode.has("result")) {
                    // 如果是对象且有 result 字段
                    content = rootNode.get("result").asText();
                } else if (rootNode.isTextual()) {
                    // 如果是纯字符串
                    content = rootNode.asText();
                }
                
                // 将转义字符转换为实际的换行符
                // 处理多种可能的转义情况
                content = content.replace("\\\\n", "\\n"); // 处理双重转义
                content = content.replace("\\n", "\n");     // 处理普通转义
                
                // 添加调试日志
                log.info("转换后内容长度: {}, 前100字符: {}", content.length(), 
                        content.length() > 100 ? content.substring(0, 100) : content);
                
            } catch (Exception e) {
                // 如果不是 JSON 格式，直接使用原响应
                log.warn("响应不是有效的 JSON 格式: {}", e.getMessage());
            }

            return content;
        } catch (Exception e) {
            log.error("调用 FastAPI 接口失败", e);
            throw new RuntimeException("调用 FastAPI 接口失败: " + e.getMessage());
        }
    }
}