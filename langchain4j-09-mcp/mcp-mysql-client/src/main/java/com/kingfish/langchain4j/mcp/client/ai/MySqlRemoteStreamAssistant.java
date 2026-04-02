package com.kingfish.langchain4j.mcp.client.ai;

import dev.langchain4j.service.TokenStream;

/**
 * MySQL 远程 MCP 流式对话助手接口
 * 返回 TokenStream，支持逐 token 流式输出，配合 SSE 推送给前端
 *
 * @author haowl
 * @date 2026/4/2 14:00
 */
public interface MySqlRemoteStreamAssistant {

    /**
     * 流式对话
     *
     * @param message 用户输入的自然语言问题
     * @return TokenStream 流式响应
     */
    TokenStream chat(String message);
}
