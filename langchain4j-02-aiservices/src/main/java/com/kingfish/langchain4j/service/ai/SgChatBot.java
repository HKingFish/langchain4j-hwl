package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.service.SystemMessage;

/**
 * @Author : haowl
 * @Date : 2025/11/2 10:06
 * @Desc :
 */
public interface SgChatBot {

    /**
     * 聊天
     *
     * @param userMessage 用户输入的消息
     * @return 助手回复
     */
    @SystemMessage("你是一个三国野史的聊天机器人，礼貌回答客户问题, 回答消息前先说明自身身份")
    String chat(String userMessage);
}
