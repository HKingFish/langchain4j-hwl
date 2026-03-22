package com.kingfish.langchain4j.observability.config;

import com.kingfish.langchain4j.observability.guardrail.DemoInputGuardrail;
import com.kingfish.langchain4j.observability.listener.AiServiceCustomEventListener;
import com.kingfish.langchain4j.observability.listener.CompletedListener;
import com.kingfish.langchain4j.observability.listener.IssuedListener;
import com.kingfish.langchain4j.observability.service.ChatAssistant;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Author : haowl
 * @Date : 2025/10/29 9:40
 * @Desc :
 */
@Slf4j
@Configuration
public class ObservabilityLLMConfig {


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

    @Bean("aliQwenModel")
    public ChatModel ImageModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("aliQwen-api"))
                .modelName("qwen2.5-omni-7b")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .build();
    }


    @Bean("aliQwenStreamingChatModel")
    public OpenAiStreamingChatModel aliQwenStreamingChatModel() {
        return OpenAiStreamingChatModel
                .builder()
                .apiKey(System.getenv("aliQwen-api"))
                .modelName("qwen2.5-omni-7b")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .build();
    }


    @Bean("chatMemoryProvider")
    public ChatMemoryProvider chatMemoryProvider(RedisChatMemoryStore chatMemoryStore) {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(20)
                .chatMemoryStore(chatMemoryStore)
                .build();
    }


    /******************************************/

    @Bean
    public ChatAssistant chatAssistant(ChatModel chatModel, ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(ChatAssistant.class)
                .chatMemoryProvider(chatMemoryProvider)
                .chatModel(chatModel)
                .inputGuardrailClasses(DemoInputGuardrail.class)
                .registerListeners(new IssuedListener(), new CompletedListener(), new AiServiceCustomEventListener())
                .build();
    }

}