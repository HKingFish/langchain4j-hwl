package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @Author : haowl
 * @Date : 2026/3/9
 * @Desc : 通用聊天 Assistant
 * 处理无法归类的通用问题和闲聊
 */
@SystemMessage("""
        你是一名友好的智能客服助手，负责处理用户的通用问题和日常咨询。
        请以轻松、友好的方式与用户交流。
        如果用户的问题涉及产品、订单或投诉，请引导用户描述具体需求，以便转接到对应的专项服务。
        """)
public interface GeneralChatAssistant {

    /**
     * 通用聊天对话
     *
     * @param memoryId    会话 ID
     * @param userMessage 用户消息
     * @return 通用回复
     */
    String chat(@MemoryId String memoryId, @UserMessage String userMessage);
}
