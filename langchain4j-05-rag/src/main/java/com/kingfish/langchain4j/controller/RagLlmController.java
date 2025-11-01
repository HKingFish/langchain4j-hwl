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
        return llmService.chat(userMessage);
    }

}