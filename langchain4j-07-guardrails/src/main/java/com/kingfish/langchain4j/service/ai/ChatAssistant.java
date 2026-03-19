package com.kingfish.langchain4j.service.ai;

/**
 * @Author : haowl
 * @Date : 2026/3/16 21:24
 * @Desc :
 */
public interface ChatAssistant {


    /**
     * 聊天
     *
     * @param userMessage
     * @return
     */
    String chat(String userMessage);
}
