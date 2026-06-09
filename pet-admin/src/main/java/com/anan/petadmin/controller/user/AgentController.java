package com.anan.petadmin.controller.user;


import com.anan.petadmin.dto.AgentDTO;
import com.anan.petadmin.service.AgentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("user/agent")
@Slf4j
public class AgentController {
    @Resource
    private AgentService agentService;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    @GetMapping()
    public SseEmitter getAgent(AgentDTO agentDTO) {
        // 增加超时时间到 3 分钟
        SseEmitter sseEmitter = new SseEmitter(180_000L);
        try {
            String result = agentService.get(agentDTO);
            if (result == null || result.isEmpty()) {
                sseEmitter.complete();
                return sseEmitter;
            }
            char[] str = result.toCharArray();
            AtomicInteger i = new AtomicInteger(0);
            int length = str.length;
            log.info("开始发送SSE数据，共 {} 个字符", length);
            
            // 提高发送速度，每 50ms 发送一次，每次发送多个字符
            ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
                try {
                    int index = i.getAndAdd(5); // 每次发送 5 个字符
                    if (index >= length) {
                        sseEmitter.complete();
                        log.info("SSE发送完成，共发送 {} 个字符", length);
                        return;
                    }
                    // 构建要发送的字符串片段
                    StringBuilder sb = new StringBuilder();
                    for (int j = index; j < Math.min(index + 5, length); j++) {
                        sb.append(str[j]);
                    }
                    sseEmitter.send(SseEmitter.event()
                            .data(sb.toString()));
                } catch (IOException e) {
                    String errorMsg = e.getMessage();
                    if (errorMsg != null && (errorMsg.contains("已建立的连接") ||
                            errorMsg.contains("Broken pipe") ||
                            errorMsg.contains("Connection reset"))) {
                        log.warn("客户端断开连接");
                    } else {
                        log.error("发送SSE数据失败", e);
                    }
                }
            }, 0, 50, TimeUnit.MILLISECONDS);

            sseEmitter.onCompletion(() -> {
                future.cancel(false);
                log.info("SSE连接正常关闭");
            });

            sseEmitter.onTimeout(() -> {
                future.cancel(false);
                log.warn("SSE连接超时");
            });

            sseEmitter.onError((throwable) -> {
                future.cancel(false);
                log.error("SSE连接错误", throwable);
            });

        } catch (Exception e) {
            log.error("处理请求失败", e);
            try {
                sseEmitter.completeWithError(e);
            } catch (Exception ex) {
                log.error("完成SSE连接失败", ex);
            }
        }
        return sseEmitter;

    }
}