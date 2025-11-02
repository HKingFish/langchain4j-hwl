package com.kingfish.langchain4j.service.ai;

/**
 * @Author : haowl
 * @Date : 2025/11/1 21:02
 * @Desc :
 */
public interface ModerationAssistant {

    /**
     * 聊天
     *
     * @param userMessage 用户消息
     * @return 助手回复
     */
    String chat(String userMessage);
}