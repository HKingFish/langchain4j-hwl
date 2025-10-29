package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

/**
 * @Author : haowl
 * @Date : 2025/10/29 13:53
 * @Desc :
 */
public interface ChatPersistenceAssistant {
    /**
     * 聊天
     *
     * @param memoryId  内存 ID
     * @param message 消息
     * @return {@link String }
     */
    String chat(@MemoryId String memoryId, @UserMessage String message);
}
