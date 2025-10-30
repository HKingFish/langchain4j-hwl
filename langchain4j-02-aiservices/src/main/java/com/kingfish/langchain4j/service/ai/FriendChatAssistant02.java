package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

/**
 * @Author : haowl
 * @Date : 2025/10/29 21:04
 * @Desc :
 */
@AiService
public interface FriendChatAssistant02 {
    /**
     * 聊天
     *
     * @param userMessage 用户消息
     * @return 助手回复
     */
    @UserMessage("你是一个抬杠大师，你要一直反驳我说的话。{{userMessage}}")
    String chat(@V("userMessage") String userMessage);


}