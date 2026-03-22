package com.kingfish.langchain4j.observability.config;

import com.kingfish.langchain4j.observability.guardrail.DemoInputGuardrail;
import com.kingfish.langchain4j.observability.listener.AiServiceCustomEventListener;
import com.kingfish.langchain4j.observability.listener.CompletedListener;
import com.kingfish.langchain4j.observability.listener.IssuedListener;
import com.kingfish.langchain4j.observability.service.ChatAssistant;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.listener.EmbeddingModelErrorContext;
import dev.langchain4j.model.embedding.listener.EmbeddingModelListener;
import dev.langchain4j.model.embedding.listener.EmbeddingModelRequestContext;
import dev.langchain4j.model.embedding.listener.EmbeddingModelResponseContext;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * @Author : haowl
 * @Date : 2025/10/29 9:40
 * @Desc :
 */
@Slf4j
@Configuration
public class ObservabilityLLMConfig {
    public static final String API_KEY = "Your Api Key";

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
                .listeners(List.of(new ChatModelListener() {
                    @Override
                    public void onRequest(ChatModelRequestContext requestContext) {
                        log.info("[onRequest][requestContext:{}]", requestContext);
                    }

                    @Override
                    public void onResponse(ChatModelResponseContext responseContext) {
                        log.info("[onResponse][responseContext:{}]", responseContext);
                    }

                    @Override
                    public void onError(ChatModelErrorContext errorContext) {
                        log.error("[onError][errorContext:{}]", errorContext);
                    }
                }))
                .modelName("gpt-4o-mini")
                .build();
    }

//    @Bean
//    public ModerationAssistant moderationAssistant() {
//        OpenAiModerationModel moderationModel = OpenAiModerationModel.builder()
//                .apiKey("demo")
//                .modelName(TEXT_MODERATION_LATEST)
//                .listeners(List.of(listener))
//                .build();
//
//        return AiServices.builder(ModerationAssistant.class)
//                .chatModel(openAiChatModel())
//                .moderationModel(moderationModel)
//                .build();
//    }


    /**
     * 初始化嵌入模型（阿里云 text-embedding-v3，支持中文）
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        OpenAiEmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .apiKey(API_KEY)
                .modelName("text-embedding-v3")
                .build();

        embeddingModel.addListener(new EmbeddingModelListener() {
            @Override
            public void onRequest(EmbeddingModelRequestContext requestContext) {
                log.info("[onRequest][requestContext:{}]", requestContext);
            }

            @Override
            public void onResponse(EmbeddingModelResponseContext responseContext) {
                log.info("[onResponse][responseContext:{}]", responseContext);
            }

            @Override
            public void onError(EmbeddingModelErrorContext errorContext) {
                log.info("[onError][errorContext:{}]", errorContext);
            }
        });

        return embeddingModel;
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