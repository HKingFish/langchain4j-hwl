package com.kingfish.langchain4j.service;

/**
 * @Author : haowl
 * @Date : 2025/10/29 9:11
 * @Desc :
 */
public interface RagLlmService {

    /**
     * 简单的问答
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    String easyChat(String userMessage);

    /**
     * 智能客服对话：根据用户意图自动路由到对应的专项处理逻辑
     *
     * @param memoryId    会话 ID，用于维持多轮对话上下文
     * @param userMessage 用户输入的消息
     * @return 客服回复内容
     */
    String customerServiceChat(String memoryId, String userMessage);

}
