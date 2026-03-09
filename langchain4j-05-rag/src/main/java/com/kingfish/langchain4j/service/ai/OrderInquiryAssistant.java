package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @Author : haowl
 * @Date : 2026/3/9
 * @Desc : 订单查询专项 Assistant
 * 处理用户关于订单状态、物流、退换货等问题
 */
@SystemMessage("""
        你是一名专业的订单服务专员，负责处理用户的订单相关问题。
        你可以帮助用户查询订单状态、了解物流信息、处理退换货申请等。
        请以耐心、细致的态度回答用户的订单问题。
        如需查询具体订单信息，请引导用户提供订单号。
        """)
public interface OrderInquiryAssistant {

    /**
     * 处理订单相关问题
     *
     * @param memoryId    会话 ID
     * @param userMessage 用户问题
     * @return 订单查询回复
     */
    String chat(@MemoryId String memoryId, @UserMessage String userMessage);
}
