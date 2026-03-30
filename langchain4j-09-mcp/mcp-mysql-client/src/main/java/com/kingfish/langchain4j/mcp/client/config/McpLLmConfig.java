package com.kingfish.langchain4j.mcp.client.config;

import com.kingfish.langchain4j.mcp.client.ai.MySqlAssistant;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

/**
 * @Author : haowl
 * @Date : 2026/3/30 21:09
 * @Desc :
 */
@Configuration
public class McpLLmConfig {

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


    @Bean
    public MySqlAssistant mySqlAssistant(ChatModel chatModel) {

        McpTransport transport = StreamableHttpMcpTransport.builder()
                .url("http://localhost:8080/langchain4j/mcp/remote-mysql/mcpTools")
                .timeout(Duration.ofSeconds(60))
                .logRequests(true) // if you want to see the traffic in the log
                .logResponses(true)
                .build();

        McpClient mcpClient = DefaultMcpClient.builder()
                .key("MysqlMcp")
                .transport(transport)
                .build();

        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();


        return AiServices.builder(MySqlAssistant.class)
                .chatModel(chatModel)
                .toolProvider(toolProvider)
                .build();
    }

}