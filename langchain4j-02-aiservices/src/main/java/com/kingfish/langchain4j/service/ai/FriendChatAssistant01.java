package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

/**
 * @Author : haowl
 * @Date : 2025/10/29 21:04
 * @Desc :
 */
@AiService
public interface FriendChatAssistant01 {
    /**
     * 聊天
     *
     * @param userMessage 用户消息
     * @return 助手回复
     */
    @SystemMessage("你是一个抬杠大师，你要一直反驳我说的话")
    String chat(String userMessage);
}