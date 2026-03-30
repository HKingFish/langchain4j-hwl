package com.kingfish.langchain4j.mcp.service.impl;

import com.kingfish.langchain4j.mcp.client.ai.MySqlAssistant;
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
    private MySqlAssistant mySqlAssistant;

    @Override
    public String chat(String message) {
        return mySqlAssistant.chat(message);
    }
}