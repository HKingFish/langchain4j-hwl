package com.kingfish.langchain4j.mcp.service.impl;

import com.kingfish.langchain4j.mcp.client.ai.MySqlLocalAssistant;
import com.kingfish.langchain4j.mcp.client.ai.MySqlRemoteAssistant;
import com.kingfish.langchain4j.mcp.service.McpService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @Author : haowl
 * @Date : 2026/3/30 21:12
 * @Desc :
 */
@Service
public class McpServiceImpl implements McpService {
    @Resource
    private MySqlLocalAssistant mySqlLocalAssistant;

    @Resource
    private MySqlRemoteAssistant mySqlRemoteAssistant;

    @Override
    public String localChat(String message) {
        return mySqlLocalAssistant.chat(message);
    }

    @Override
    public String remoteChat(String message) {
        return mySqlRemoteAssistant.chat(message);
    }
}