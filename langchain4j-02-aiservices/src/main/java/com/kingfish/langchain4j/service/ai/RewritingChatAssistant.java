package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.service.SystemMessage;

/**
 * @Author : haowl
 * @Date : 2025/11/1 21:02
 * @Desc :
 */
public interface RewritingChatAssistant {

    /**
     * 聊天
     *
     * @param userMessage 用户消息
     * @return 助手回复
     */
    @SystemMessage("你是一个专业的聊天助手")
    String chat(String userMessage);
}