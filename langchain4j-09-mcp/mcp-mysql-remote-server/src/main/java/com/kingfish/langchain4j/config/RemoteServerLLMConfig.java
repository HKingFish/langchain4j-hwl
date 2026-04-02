package com.kingfish.langchain4j.config;

import com.kingfish.langchain4j.service.ai.MySqlStreamAssistant;
import com.kingfish.langchain4j.tools.MySqlMcpDynamicTool;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Remote Server 端 LLM 配置类
 * 注册 StreamingChatModel 和流式 MySQL 对话助手
 *
 * @author haowl
 * @date 2026/4/2 14:00
 */
@Configuration
public class RemoteServerLLMConfig {

    /**
     * OpenAI 流式模型
     */
    @Bean
    public OpenAiStreamingChatModel openAiStreamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();
    }

    /**
     * 流式 MySQL 对话助手
     * 绑定 StreamingChatModel 和 MySqlMcpDynamicTool，支持工具调用 + 流式输出
     *
     * @param streamingChatModel 流式模型
     * @param mySqlMcpDynamicTool MySQL MCP 工具集
     * @return 流式助手实例
     */
    @Bean
    public MySqlStreamAssistant mySqlStreamAssistant(OpenAiStreamingChatModel streamingChatModel,
                                                     MySqlMcpDynamicTool mySqlMcpDynamicTool) {
        return AiServices.builder(MySqlStreamAssistant.class)
                .streamingChatModel(streamingChatModel)
                .tools(mySqlMcpDynamicTool)
                .build();
    }

    /**
     * 将 MySqlMcpDynamicTool 注册为 Spring Bean，供 AiServices 注入
     */
    @Bean
    public MySqlMcpDynamicTool mySqlMcpDynamicTool() {
        return new MySqlMcpDynamicTool();
    }
}
