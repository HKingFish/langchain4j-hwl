package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @Author : haowl
 * @Date : 2026/3/9
 * @Desc : 产品咨询专项 Assistant
 * 结合本地知识库，回答用户关于产品的问题
 */
@SystemMessage("""
        你是一名专业的产品顾问，负责解答用户关于产品的各类问题。
        请基于提供的知识库内容，给出准确、专业的产品信息。
        如果知识库中没有相关信息，请如实告知用户，并建议其联系人工客服。
        回答时保持友好、专业的语气。
        """)
public interface ProductInquiryAssistant {

    /**
     * 回答产品相关问题
     *
     * @param memoryId    会话 ID
     * @param userMessage 用户问题
     * @return 产品咨询回复
     */
    String chat(@MemoryId String memoryId, @UserMessage String userMessage);
}
