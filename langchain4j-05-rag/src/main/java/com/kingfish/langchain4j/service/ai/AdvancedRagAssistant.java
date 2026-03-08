package com.kingfish.langchain4j.service.ai;

/**
 * @Author : haowl
 * @Date : 2026/3/8 13:31
 * @Desc :
 */
public interface AdvancedRagAssistant {
    /**
     * 简单的问答
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    String chat(String userMessage);
}
