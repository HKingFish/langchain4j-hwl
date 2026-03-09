package com.kingfish.langchain4j.service;

import com.kingfish.langchain4j.service.ai.CustomerServiceAssistant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author : haowl
 * @Date : 2026/3/9
 * @Desc : 智能客服调度器
 * 意图路由已下沉至 DefaultRetrievalAugmentor 的 queryRouter 中处理，
 * 此处直接委托给统一的 CustomerServiceAssistant
 */
@Slf4j
@Service
public class CustomerServiceDispatcher {

    @Resource
    private CustomerServiceAssistant customerServiceAssistant;

    /**
     * 智能客服对话入口
     * 意图识别和知识库路由由 RAG 流水线内部完成：
     * - PRODUCT_INQUIRY → 检索产品知识库，将相关内容注入上下文
     * - 其他意图        → 不检索知识库，由 SystemMessage 提示词驱动回答
     *
     * @param memoryId    会话 ID
     * @param userMessage 用户消息
     * @return 客服回复
     */
    public String chat(String memoryId, String userMessage) {
        log.info("智能客服收到请求：memoryId={}, message={}", memoryId, userMessage);
        return customerServiceAssistant.chat(memoryId, userMessage);
    }
}
