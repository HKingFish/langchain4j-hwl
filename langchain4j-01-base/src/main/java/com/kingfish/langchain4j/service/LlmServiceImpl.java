package com.kingfish.langchain4j.service;

import com.kingfish.langchain4j.config.RedisChatMemoryStore;
import com.kingfish.langchain4j.service.ai.ChatPersistenceAssistant;
import dev.langchain4j.data.message.*;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.PartialResponse;
import dev.langchain4j.model.chat.response.PartialResponseContext;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.*;


/**
 * @Author : haowl
 * @Date : 2025/10/29 9:11
 * @Desc :
 */
@Service
public class LlmServiceImpl implements LlmService {

    /**
     * 历史记录的问答
     */
    private final List<ChatMessage> messageHistory = new ArrayList<>();

    public final Map<String, ChatMemory> chatMemoryCache = new HashMap<>();

    @Value("classpath:static/images/pic1.png")
    private org.springframework.core.io.Resource resource;

    @Resource
    private OpenAiChatModel chatModel;

    @Resource(name = "aliQwenModel")
    private ChatModel aliQwenModel;

    @Resource(name = "openAiStreamingChatModel")
    private OpenAiStreamingChatModel openAiStreamingChatModel;

    @Resource(name = "aliQwenStreamingChatModel")
    private OpenAiStreamingChatModel aliQwenStreamingChatModel;

    @Resource
    private ChatPersistenceAssistant chatPersistenceAssistant;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Override
    public String chat(String userMessage) {
        return chatModel.chat(userMessage);
    }

    @Override
    public AiMessage chatWithMultimodality(String message) throws IOException {
        // 图片 转 base64
        String base64Data = Base64.getEncoder().encodeToString(resource.getContentAsByteArray());

        // 构建多模态消息
        UserMessage userMessage = UserMessage.from(
                TextContent.from(message),
                ImageContent.from(base64Data, MimeTypeUtils.IMAGE_PNG_VALUE)
        );
        // 切换模型是因为 LangChain4J 官方给的apiKey token 长度不够，无法处理图片
        return aliQwenModel.chat(userMessage).aiMessage();
    }


    @Override
    public AiMessage chatWithHistory(String message) {
        UserMessage userMessage = UserMessage.from(message);
        // TODO : 可以将历史记录持久化到 DB 中
        messageHistory.add(userMessage);
        return chatModel.chat(messageHistory).aiMessage();
    }


    @Override
    public AiMessage chatWithLowLevelPersistentHistory(String memoryId, String message) {
        UserMessage userMessage = UserMessage.from(message);

        ChatMemory chatMemory = chatMemoryCache.computeIfAbsent(memoryId,
                mId -> MessageWindowChatMemory.builder()
                        // 用户id 或者会话id
                        .id(mId)
                        // 最大消息数
                        .maxMessages(10)
                        // 存储方式
                        .chatMemoryStore(redisChatMemoryStore)
                        .build());

        chatMemory.add(userMessage);
        return chatModel.chat(chatMemory.messages()).aiMessage();
    }

    @Override
    public String chatWithHighLevelPersistentHistory(String memoryId, String message) {
        return chatPersistenceAssistant.chat(memoryId, message);
    }

    @Override
    public Flux<String> streamChat(String message) {
        return Flux.create(emitter ->
                aliQwenStreamingChatModel.chat(message, new StreamingChatResponseHandler() {


                    @Override
                    public void onPartialResponse(PartialResponse partialResponse, PartialResponseContext context) {
                        emitter.next(partialResponse.text());
                        // 判断是否需要取消请求
//                        if (shouldCancel()) {
//                            context.streamingHandle().cancel();
//                        }
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse chatResponse) {
                        emitter.complete();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        emitter.error(throwable);
                    }
                }));
    }
}
