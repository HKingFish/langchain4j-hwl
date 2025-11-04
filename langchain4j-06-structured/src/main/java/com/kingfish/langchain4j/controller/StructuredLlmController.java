package com.kingfish.langchain4j.controller;

import com.kingfish.langchain4j.service.StructuredLlmService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author : haowl
 * @Date : 2025/10/28 20:53
 * @Desc :
 */
@RestController
@RequestMapping("/llm/structured")
public class StructuredLlmController {

    @Resource
    private StructuredLlmService llmService;

    /**
     * 简单的问答
     * -  约翰今年42岁，过着独立的生活。他身高1.75米，举止自信。目前未婚，他享有专注于个人目标和兴趣的自由。
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    @RequestMapping("/chatByChatModel")
    public String chatByChatModel(String userMessage) {
        return llmService.chatByChatModel(userMessage);
    }

    /**
     * 从文本中提取Person对象
     *
     * @param userMessage 包含Person信息的文本
     * @return 提取到的Person对象
     */
    @RequestMapping("/chatByAiService")
    public String chatByAiService(String userMessage) {
        return llmService.chatByAiService(userMessage);
    }
}