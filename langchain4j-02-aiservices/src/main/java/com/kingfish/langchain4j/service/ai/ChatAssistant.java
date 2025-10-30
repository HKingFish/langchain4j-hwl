package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.service.spring.AiService;

/**
 * @Author : haowl
 * @Date : 2025/10/29 16:47
 * @Desc : 聊天助手接口
 */
@AiService
public interface ChatAssistant {
    /**
     * 聊天
     *
     * @param userMessage 用户消息
     * @return 助手回复
     */
    String chat(String userMessage);

}
