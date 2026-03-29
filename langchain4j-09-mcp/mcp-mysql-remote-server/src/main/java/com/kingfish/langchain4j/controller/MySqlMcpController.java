package com.kingfish.langchain4j.controller;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingfish.langchain4j.tools.MySqlMcpDynamicTool;
import dev.langchain4j.community.mcp.server.McpServer;
import dev.langchain4j.mcp.protocol.McpImplementation;
import dev.langchain4j.mcp.protocol.McpJsonRpcMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author : haowl
 * @Date : 2026/3/29 18:16
 * @Desc :
 */

@RestController
@RequestMapping("/mcp/remote-mysql")
public class MySqlMcpController {

    private final McpServer mcpServer;

    public MySqlMcpController() {
        MySqlMcpDynamicTool mysqlTool = new MySqlMcpDynamicTool();
        McpImplementation serverInfo = new McpImplementation();
        serverInfo.setName("mysql-mcp-server");
        serverInfo.setVersion("1.0.0");

        // 创建 MCP 服务器并注册工具
        this.mcpServer = new McpServer(List.of(mysqlTool), serverInfo);
    }

    @PostMapping("/mcpTools")
    public String mcpTools(@RequestBody String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(JSONUtil.toJsonStr(json));
        McpJsonRpcMessage rpcMessage = mcpServer.handle(jsonNode);
        return rpcMessage.jsonrpc;
    }
}
