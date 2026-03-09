package com.kingfish.langchain4j.controller;

import com.kingfish.langchain4j.service.RagLlmService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author : haowl
 * @Date : 2025/10/28 20:53
 * @Desc :
 */
@RestController
@RequestMapping("/llm/rag")
public class RagLlmController {

    @Resource
    private RagLlmService llmService;

    /**
     * 简单的问答
     * - 张三的老婆是谁
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    @RequestMapping("/chat")
    public String chat(String userMessage) {
        return llmService.easyChat(userMessage);
    }


    /**
     * 智能客服对话：根据用户意图自动路由到对应的专项处理逻辑
     * 意图分类：
     * - PRODUCT_INQUIRY（产品咨询）：结合知识库回答产品相关问题
     * - ORDER_INQUIRY（订单查询）：处理订单状态、物流、退换货等问题
     * - COMPLAINT（投诉建议）：处理用户投诉和改进建议
     * - GENERAL_CHAT（通用聊天）：处理无法归类的通用问题
     *
     * @param memoryId    会话 ID，用于维持多轮对话上下文
     * @param userMessage 用户输入的消息
     * @return 客服回复内容
     */
    @RequestMapping("/customerServiceChat")
    public String customerServiceChat(String memoryId, String userMessage) {
        return llmService.customerServiceChat(memoryId, userMessage);
    }
}