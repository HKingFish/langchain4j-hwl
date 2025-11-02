package com.kingfish.langchain4j.service;

import dev.langchain4j.data.message.AiMessage;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * @Author : haowl
 * @Date : 2025/10/29 9:11
 * @Desc :
 */
public interface BaseLlmService {


    /**
     * 简单的问答
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    String chat(String userMessage);

    /**
     * 多模态问答
     *
     * @param message 用户输入的消息
     * @return 模型返回的消息
     */
    AiMessage chatWithMultimodality(String message) throws IOException;


    /**
     * 带历史记录的问答
     *
     * @param message 用户输入的消息
     * @return 模型返回的消息
     */
    AiMessage chatWithHistory(String message);


    /**
     * 带持久化历史记录的问答 - low level
     *
     * @param message 用户输入的消息
     * @return 模型返回的消息
     */
    AiMessage chatWithLowLevelPersistentHistory(String memoryId, String message);

    /**
     * 带持久化历史记录的问答 - high level
     *
     * @param message 用户输入的消息
     * @return 模型返回的消息
     */
    String chatWithHighLevelPersistentHistory(String memoryId, String message);


    /**
     * 流式问答
     *
     * @param message 用户输入的消息
     * @return 模型返回的消息流
     */
    Flux<String> streamChat(String message);

}
