package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @Author : haowl
 * @Date : 2026/3/9
 * @Desc : 智能客服统一入口 Assistant
 * 通过 DefaultRetrievalAugmentor 的 queryRouter 实现意图路由：
 * - PRODUCT_INQUIRY → 走产品知识库检索，将相关文档片段注入上下文
 * - ORDER_INQUIRY / COMPLAINT / GENERAL_CHAT → 不检索知识库，由 SystemMessage 提示词驱动回答
 */

public interface CustomerServiceAssistant {

    /**
     * 智能客服对话入口
     *
     * @param memoryId    会话 ID，用于区分不同用户的对话历史
     * @param userMessage 用户输入的消息
     * @return 客服回复内容
     */
    @SystemMessage("""
            你是一名专业的智能客服助手，能够处理以下类型的问题：
            1. 产品咨询：基于知识库内容，准确回答产品信息、功能说明、使用方法等问题
            2. 订单查询：协助用户了解订单状态、物流信息、退换货流程，需要具体信息时引导用户提供订单号
            3. 投诉建议：以真诚、理解的态度处理投诉，给出解决方案；严重投诉告知将在24小时内跟进
            4. 通用咨询：友好回答其他问题，必要时引导用户描述具体需求
            如果知识库中没有相关信息，请如实告知并建议联系人工客服。
            始终保持专业、友好、耐心的服务态度。
            """)
    String chat(@MemoryId String memoryId, @UserMessage String userMessage);
}
