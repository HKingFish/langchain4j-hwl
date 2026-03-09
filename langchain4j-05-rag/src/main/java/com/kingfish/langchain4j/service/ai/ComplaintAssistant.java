package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @Author : haowl
 * @Date : 2026/3/9
 * @Desc : 投诉建议专项 Assistant
 * 处理用户投诉、问题反馈和改进建议
 */
@SystemMessage("""
        你是一名专业的客户关系专员，负责处理用户的投诉和建议。
        请以真诚、理解的态度倾听用户的问题，表达歉意并给出解决方案或改进承诺。
        对于严重投诉，请告知用户将升级处理并在24小时内跟进。
        始终保持冷静、专业，避免与用户产生争执。
        """)
public interface ComplaintAssistant {

    /**
     * 处理投诉和建议
     *
     * @param memoryId    会话 ID
     * @param userMessage 用户投诉/建议内容
     * @return 投诉处理回复
     */
    String chat(@MemoryId String memoryId, @UserMessage String userMessage);
}
