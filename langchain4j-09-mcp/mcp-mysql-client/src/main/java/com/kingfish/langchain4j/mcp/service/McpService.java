package com.kingfish.langchain4j.mcp.service;

import reactor.core.publisher.Flux;

/**
 * @Author : haowl
 * @Date : 2026/3/30 21:12
 * @Desc :
 */
public interface McpService {
    String localChat(String message);

    String remoteChat(String message);

    /**
     * 远程 MCP 流式对话（SSE）
     *
     * @param message 用户输入的自然语言问题
     * @return Flux<String> 流式响应
     */
    Flux<String> remoteStreamChat(String message);
}
