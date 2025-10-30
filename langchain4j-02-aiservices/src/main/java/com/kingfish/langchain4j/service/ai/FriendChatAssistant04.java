package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.memory.ChatMemoryAccess;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

/**
 * @Author : haowl
 * @Date : 2025/10/29 21:04
 * @Desc :
 */
@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,
        streamingChatModel = "aliQwenStreamingChatModel",
        chatMemoryProvider = "chatMemoryProvider")
public interface FriendChatAssistant04 extends ChatMemoryAccess {

    @SystemMessage("你是一个抬杠大师，你要一直反驳我说的话。")
    Flux<String> chat01(String userMessage);

    @SystemMessage("你是一个抬杠大师，你要一直反驳我说的话。")
    TokenStream chat02(String userMessage);
}