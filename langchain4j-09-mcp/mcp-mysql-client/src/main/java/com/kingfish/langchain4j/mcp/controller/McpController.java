package com.kingfish.langchain4j.mcp.controller;

import com.kingfish.langchain4j.mcp.service.McpService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @RequestMapping("/chat")
    public String chat(String message) {
        return mcpService.chat(message);
    }
}