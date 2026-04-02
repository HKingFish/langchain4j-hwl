package com.kingfish.langchain4j.mcp.controller;

import com.kingfish.langchain4j.mcp.service.McpService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @Author : haowl
 * @Date : 2026/3/30 21:13
 * @Desc :
 */
@RequestMapping("/mcp")
@RestController
public class McpController {

    @Resource
    private McpService mcpService;

    @RequestMapping("/localChat")
    public String localChat(String message) {
        return mcpService.localChat(message);
    }

    @RequestMapping("/remoteChat")
    public String remoteChat(String message) {
        return mcpService.remoteChat(message);
    }

    /**
     * 远程 MCP 流式对话接口（SSE）
     * 逐 token 推送 LLM 响应，前端可通过 EventSource 接收
     *
     * @param message 用户输入的自然语言问题
     * @return Flux<String> SSE 流式响应
     */
    @RequestMapping(value = "/remoteStreamChat", produces = "text/event-stream")
    public Flux<String> remoteStreamChat(String message) {
        return mcpService.remoteStreamChat(message);
    }
}