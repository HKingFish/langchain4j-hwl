package com.kingfish.langchain4j.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingfish.langchain4j.service.ai.MySqlStreamAssistant;
import com.kingfish.langchain4j.tools.MySqlMcpDynamicTool;
import dev.langchain4j.community.mcp.server.McpServer;
import dev.langchain4j.mcp.protocol.McpImplementation;
import dev.langchain4j.service.TokenStream;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * MySQL MCP 远程服务控制器
 * 通过 Streamable HTTP 方式暴露 MCP 协议端点，支持 initialize、tools/list、tools/call 等全部 MCP 方法
 * 同时提供流式对话接口，支持 SSE 逐 token 推送
 *
 * @author haowl
 * @date 2026/3/29 18:16
 */
@RestController
@RequestMapping("/mcp/remote-mysql")
public class MySqlMcpController {

    private static final Logger log = LoggerFactory.getLogger(MySqlMcpController.class);

    private final McpServer mcpServer;
    private final ObjectMapper objectMapper;

    @Resource
    private MySqlStreamAssistant mySqlStreamAssistant;

    public MySqlMcpController(MySqlMcpDynamicTool mySqlMcpDynamicTool) {
        McpImplementation serverInfo = new McpImplementation();
        serverInfo.setName("mysql-mcp-server");
        serverInfo.setVersion("1.0.0");

        // 创建 MCP 服务器并注册工具
        this.mcpServer = new McpServer(List.of(mySqlMcpDynamicTool), serverInfo);
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 处理 MCP 协议的 JSON-RPC 请求
     * 支持 initialize、tools/list、tools/call 等所有 MCP 方法，返回通用 JsonNode 避免类型强转
     *
     * @param json 客户端发送的 JSON-RPC 请求体
     * @return JSON-RPC 响应（类型取决于请求方法）
     * @throws JsonProcessingException JSON 解析异常
     */
    @PostMapping("/mcpTools")
    public JsonNode mcpTools(@RequestBody String json) throws JsonProcessingException {
        JsonNode requestNode = objectMapper.readTree(json);
        log.info("收到 MCP 请求，方法：{}", requestNode.path("method").asText());
        Object result = mcpServer.handle(requestNode);
        log.info("MCP 响应：{}", result);
        return objectMapper.valueToTree(result);
    }

    /**
     * 流式对话接口（SSE）
     * 接收用户自然语言问题，LLM 自动决策调用 MySQL 工具，逐 token 以 SSE 方式推送结果
     *
     * @param message 用户输入的自然语言问题
     * @return Flux<String> SSE 流式响应
     */
    @RequestMapping(value = "/streamChat", produces = "text/event-stream")
    public Flux<String> streamChat(String message) {
        log.info("收到流式对话请求：{}", message);
        TokenStream tokenStream = mySqlStreamAssistant.chat(message);
        return Flux.create(sink ->
                tokenStream.onPartialResponse(sink::next)
                        .onCompleteResponse(response -> {
                            log.info("流式对话完成");
                            sink.complete();
                        })
                        .onError(error -> {
                            log.error("流式对话异常：{}", error.getMessage());
                            sink.error(error);
                        })
                        .start()
        );
    }
}
