package com.kingfish.langchain4j.service;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @Author : haowl
 * @Date : 2025/10/29 9:11
 * @Desc :
 */
public interface AiServiceLlmService {
    /**
     * 简单的问答
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    String chat(String userMessage);


    /**
     * 带系统提示的聊天
     *
     * @param userMessage 用户消息
     * @return 助手回复
     */
    String chatWithSystemMessage(String userMessage);


    /**
     * 带用户提示的聊天
     *
     * @param userMessage 用户消息
     * @return 助手回复
     */
    String chatWithUserMessage(String userMessage);

    /**
     * 返回 Result 类型的聊天
     *
     * @param userMessage 用户消息
     * @return 助手回复
     */
    List<String> chatWithResult(String userMessage);


    /**
     * 流式聊天
     *
     * @param userMessage 用户消息
     * @return 助手回复
     */
    Flux<String> chatWithStreaming01(String userMessage);

    /**
     * 流式聊天 - 02
     *
     * @param userMessage 用户消息
     * @return 助手回复
     */
    Flux<String> chatWithStreaming02(String userMessage);


    /**
     * 带内容审核的聊天
     *
     * @param userMessage 用户消息
     * @return 助手回复
     */
    String chatWithModeration(String userMessage);

    /**
     * 带重写的聊天
     *
     * @param userMessage 用户消息
     * @return 助手回复
     */
    String chatWithRewriting(String userMessage);

    /**
     * 带三国野史的问答
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    String chatWithSg(String userMessage);
}
