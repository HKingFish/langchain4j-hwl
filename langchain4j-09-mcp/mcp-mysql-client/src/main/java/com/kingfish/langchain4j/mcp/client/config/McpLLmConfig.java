package com.kingfish.langchain4j.mcp.client.config;

import com.kingfish.langchain4j.mcp.client.ai.MySqlLocalAssistant;
import com.kingfish.langchain4j.mcp.client.ai.MySqlRemoteAssistant;
import com.kingfish.langchain4j.mcp.client.ai.MySqlRemoteStreamAssistant;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpCallContext;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.McpClientListener;
import dev.langchain4j.mcp.client.logging.McpLogMessage;
import dev.langchain4j.mcp.client.logging.McpLogMessageHandler;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * MCP 大模型配置类
 * 负责配置 OpenAI 模型、远程/本地 MCP 客户端及对应的 AI 助手
 *
 * @author haowl
 * @date 2026/3/30 21:09
 */
@Slf4j
@Configuration
public class McpLLmConfig {

    /**
     * 本地 MCP Server JAR 文件名（位于 classpath 下）
     */
    private static final String LOCAL_SERVER_JAR_NAME = "mcp-mysql-local-server-1.0-SNAPSHOT.jar";

    /**
     * MCP 传输层超时时间（秒）
     */
    private static final int MCP_TRANSPORT_TIMEOUT_SECONDS = 60;

    /**
     * MySQL 连接地址
     */
    private static final String MYSQL_JDBC_URL = "jdbc:mysql://localhost:3306/demo";

    /**
     * MySQL 用户名
     */
    private static final String MYSQL_USERNAME = "root";

    /**
     * MySQL 密码
     */
    private static final String MYSQL_PASSWORD = "root";

    /**
     * OpenAi模型
     *
     * @return
     * @link <a href="https://docs.langchain4j.dev/get-started/">...</a>
     */
    @Bean
    @Primary
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();
    }

    /**
     * OpenAI 流式模型，用于流式对话
     */
    @Bean
    public OpenAiStreamingChatModel openAiStreamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();
    }


    @Bean
    public MySqlRemoteAssistant mySqlRemoteAssistant(ChatModel chatModel) {
        McpTransport transport = StreamableHttpMcpTransport.builder()
                .url("http://localhost:8080/langchain4j/mcp/remote-mysql/mcpTools")
                .timeout(Duration.ofSeconds(MCP_TRANSPORT_TIMEOUT_SECONDS))
                .logRequests(true) // if you want to see the traffic in the log
                .logResponses(true)
                .build();

        McpClient mcpClient = DefaultMcpClient.builder()
                .key("MysqlRemoteMcp")
                .transport(transport)
                .build();

        // 打印出每个工具的具体信息
        for (ToolSpecification toolSpecification : mcpClient.listTools()) {
            log.info("远程 MCP 工具：{}", toolSpecification);
        }

        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();


        return AiServices.builder(MySqlRemoteAssistant.class)
                .chatModel(chatModel)
                .toolProvider(toolProvider)
                .build();
    }

    @Bean
    public MySqlLocalAssistant mySqlLocalAssistant(ChatModel chatModel) {
        // 从 classpath 提取本地 MCP Server JAR 到临时目录（打包后 JAR 嵌入主 JAR，无法直接引用）
        String jarPath = extractLocalServerJar();

        McpTransport transport = StdioMcpTransport.builder()
                .command(List.of(
                        "java", "-jar", jarPath,
                        MYSQL_JDBC_URL, MYSQL_USERNAME, MYSQL_PASSWORD
                ))
                .build();

        McpClient mcpClient = DefaultMcpClient.builder()
                .key("MysqlLocalMcp")
                .listener(new McpClientListener() {
                    @Override
                    public void beforeExecuteTool(McpCallContext context) {
                        log.info("本地 MCP 执行工具前：{}", context);
                    }

                    @Override
                    public void afterExecuteTool(McpCallContext context, ToolExecutionResult result, Map<String, Object> rawResult) {
                        log.info("本地 MCP 执行工具后后：{}", result);
                    }
                })
                .transport(transport)
                .build();

        // 打印出每个工具的具体信息
        for (ToolSpecification toolSpecification : mcpClient.listTools()) {
            log.info("本地 MCP 工具：{}", toolSpecification);
        }

        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();

        return AiServices.builder(MySqlLocalAssistant.class)
                .chatModel(chatModel)
                .toolProvider(toolProvider)
                .build();
    }

    /**
     * 远程 MCP 流式对话助手
     * 通过 StreamableHttp 连接远程 MCP Server，使用 StreamingChatModel 实现逐 token 流式输出
     *
     * @param streamingChatModel 流式模型
     * @return 流式助手实例
     */
    @Bean
    public MySqlRemoteStreamAssistant mySqlRemoteStreamAssistant(OpenAiStreamingChatModel streamingChatModel) {
        McpTransport transport = StreamableHttpMcpTransport.builder()
                .url("http://localhost:8080/langchain4j/mcp/remote-mysql/mcpTools")
                .timeout(Duration.ofSeconds(MCP_TRANSPORT_TIMEOUT_SECONDS))
                .logRequests(true)
                .logResponses(true)
                .build();

        McpClient mcpClient = DefaultMcpClient.builder()
                .key("MysqlRemoteStreamMcp")
                .transport(transport)
                .build();

        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();

        return AiServices.builder(MySqlRemoteStreamAssistant.class)
                .streamingChatModel(streamingChatModel)
                .toolProvider(toolProvider)
                .build();
    }

    /**
     * 从 classpath 提取本地 MCP Server JAR 到系统临时目录
     * 打包后 resources 中的 JAR 嵌入主 JAR 内部，无法直接通过文件路径访问，
     * 因此需要提取到临时文件供 java -jar 命令启动
     *
     * @return 提取后的 JAR 文件绝对路径
     * @throws IllegalStateException 提取失败时抛出
     */
    private String extractLocalServerJar() {
        try {
            ClassPathResource resource = new ClassPathResource(LOCAL_SERVER_JAR_NAME);
            Path tempJar = Files.createTempFile("mcp-mysql-local-server-", ".jar");
            // JVM 退出时自动清理临时文件
            tempJar.toFile().deleteOnExit();

            try (InputStream inputStream = resource.getInputStream()) {
                Files.copy(inputStream, tempJar, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("本地 MCP Server JAR 已提取到临时路径：{}", tempJar);
            return tempJar.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("提取本地 MCP Server JAR 失败，文件名：{}，异常信息：{}",
                    LOCAL_SERVER_JAR_NAME, e.getMessage());
            throw new IllegalStateException("无法加载本地 MCP Server JAR：" + LOCAL_SERVER_JAR_NAME, e);
        }
    }


}