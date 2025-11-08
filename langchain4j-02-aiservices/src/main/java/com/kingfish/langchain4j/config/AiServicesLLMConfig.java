package com.kingfish.langchain4j.config;

import com.kingfish.langchain4j.service.ai.*;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiModerationModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

import static dev.langchain4j.model.openai.OpenAiModerationModelName.TEXT_MODERATION_LATEST;

/**
 * @Author : haowl
 * @Date : 2025/10/29 9:40
 * @Desc :
 */
@Slf4j
@Configuration
public class AiServicesLLMConfig {


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

    @Bean("aliQwenStreamingChatModel")
    public OpenAiStreamingChatModel aliQwenStreamingChatModel() {
        return OpenAiStreamingChatModel
                .builder()
                .apiKey(System.getenv("aliQwen-api"))
                .modelName("qwen2.5-omni-7b")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .build();
    }


    @Bean
    public ChatMemoryProvider chatMemoryProvider(RedisChatMemoryStore chatMemoryStore) {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(chatMemoryStore)
                .build();
    }


//    @Bean
//    public FriendChatAssistant04 friendChatAssistant04(ChatMemoryProvider chatMemoryProvider) {
//        return AiServices.builder(FriendChatAssistant04.class)
//                .chatModel(openAiChatModel())
//                .chatMemoryProvider(chatMemoryProvider::get)
//                .build();
//    }

    @Bean
    public ModerationAssistant moderationAssistant() {
        OpenAiModerationModel moderationModel = OpenAiModerationModel.builder()
                .apiKey("demo")
                .modelName(TEXT_MODERATION_LATEST)
                .build();

        return AiServices.builder(ModerationAssistant.class)
                .chatModel(openAiChatModel())
                .moderationModel(moderationModel)
                .build();
    }

    @Bean
    public RewritingChatAssistant rewritingChatAssistant() {
        return AiServices.builder(RewritingChatAssistant.class)
                .chatModel(openAiChatModel())
                .chatRequestTransformer((chatRequest, memoryId) -> {
                    log.info("memoryId: {}, chatRequest: {}", memoryId, chatRequest);

                    // 添加额外信息
                    List<ChatMessage> messages = new ArrayList<>(List.of(UserMessage.from("张三跟王五是亲兄弟")));
                    messages.addAll(chatRequest.messages());

                    return ChatRequest.builder()
                            .messages(messages).build();
                })
                .build();
    }

    @Bean
    public SgChatBotDelegate sgChatBotDelegate(ChatModel chatModel) {
        // TODO : 可以指定更加便宜的模型来进行 GreetingExpert 的构建
        GreetingExpert greetingExpert = AiServices.builder(GreetingExpert.class)
                .chatModel(chatModel)
                .build();

        List<Document> documents = ClassPathDocumentLoader.loadDocuments("static/knowledge");
        // 构建一个内存中的向量数据库（EmbeddingStore），用于存储文档的向量表示。
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        SgChatBot chatBotAssistant = AiServices.builder(SgChatBot.class)
                .chatModel(chatModel)
//                .systemMessageProvider(o -> "你是一个三国野史的聊天机器人，回答消息前先说明自身身份")
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        return new SgChatBotDelegate(greetingExpert, chatBotAssistant);
    }

}
