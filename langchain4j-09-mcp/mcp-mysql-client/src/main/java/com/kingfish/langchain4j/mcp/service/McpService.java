package com.kingfish.langchain4j.mcp.service;

/**
 * @Author : haowl
 * @Date : 2026/3/30 21:12
 * @Desc :
 */
public interface McpService {
    String localChat(String message);

    String remoteChat(String message);
}
