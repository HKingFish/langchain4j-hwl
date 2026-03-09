package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @Author : haowl
 * @Date : 2026/3/9
 * @Desc : 智能客服意图分类路由器
 * 负责识别用户意图，将请求路由到对应的处理逻辑
 */
public interface CustomerServiceRouter {

    /**
     * 用户意图分类枚举
     *
     * @see CustomerServiceRouter#classify(String)
     */
    enum UserIntent {
        /**
         * 产品/知识咨询：用户询问产品信息、功能说明、使用方法等
         */
        PRODUCT_INQUIRY,
        /**
         * 订单相关：用户询问订单状态、物流、退换货等
         */
        ORDER_INQUIRY,
        /**
         * 投诉建议：用户反馈问题、提出投诉或改进建议
         */
        COMPLAINT,
        /**
         * 通用聊天：闲聊或无法归类的其他问题
         */
        GENERAL_CHAT
    }

    /**
     * 对用户消息进行意图分类
     *
     * @param userMessage 用户输入的消息
     * @return 用户意图分类
     */
    @SystemMessage("""
            你是一个智能客服意图分类器。
            请根据用户的消息，将其归类为以下四种意图之一：
            - PRODUCT_INQUIRY：用户在询问产品信息、功能说明、使用方法、价格等产品相关问题
            - ORDER_INQUIRY：用户在询问订单状态、物流信息、退换货、支付等订单相关问题
            - COMPLAINT：用户在投诉、反馈问题或提出改进建议
            - GENERAL_CHAT：闲聊或无法归类的其他问题
            只返回枚举值，不要返回任何其他内容。
            """)
    UserIntent classify(@UserMessage String userMessage);
}
