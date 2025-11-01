package com.kingfish.langchain4j.service;

import com.kingfish.langchain4j.service.ai.*;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;


/**
 * @Author : haowl
 * @Date : 2025/10/29 9:11
 * @Desc :
 */
@Service
public class AiServiceLlmServiceImpl implements AiServiceLlmService {

    @Resource
    private ChatAssistant chatAssistant;

    @Resource
    private FriendChatAssistant01 friendChatAssistant01;

    @Resource
    private FriendChatAssistant02 friendChatAssistant02;

    @Resource
    private FriendChatAssistant03 friendChatAssistant03;

    @Resource
    private FriendChatAssistant04 friendChatAssistant04;


    @Override
    public String chat(String userMessage) {
        return chatAssistant.chat(userMessage);
    }

    @Override
    public String chatWithSystemMessage(String userMessage) {
        return friendChatAssistant01.chat(userMessage);
    }

    @Override
    public String chatWithUserMessage(String userMessage) {
        return friendChatAssistant02.chat(userMessage);
    }

    @Override
    public List<String> chatWithResult(String userMessage) {
        Result<List<String>> result = friendChatAssistant03.chat(userMessage);
        List<String> outline = result.content();
        // TODO : 可以获取其他的额外信息
        TokenUsage tokenUsage = result.tokenUsage();
        List<Content> sources = result.sources();
        List<ToolExecution> toolExecutions = result.toolExecutions();
        FinishReason finishReason = result.finishReason();
        return outline;
    }

    @Override
    public Flux<String> chatWithStreaming01(String userMessage) {
        return friendChatAssistant04.chat01(userMessage);
    }

    @Override
    public Flux<String> chatWithStreaming02(String userMessage) {
        TokenStream chat = friendChatAssistant04.chat02(userMessage);
        return Flux.create(fluxSink ->
                chat.onPartialResponse(fluxSink::next)
                        .onCompleteResponse(chatResponse -> fluxSink.complete())
                        .onError(fluxSink::error)
                        .start());
    }
}
