package com.kingfish.langchain4j.controller;

import com.kingfish.langchain4j.service.LlmService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;


/**
 * @Author : haowl
 * @Date : 2025/10/28 20:53
 * @Desc :
 */
@RestController
@RequestMapping("/llm")
public class LlmController {

    @Resource
    private LlmService llmService;

    /**
     * 简单的问答
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    @RequestMapping("/chat")
    public String chat(String userMessage) {
        return llmService.chat(userMessage);
    }


    /**
     * 带系统提示的聊天
     *
     * @param userMessage 用户输入的消息
     * @return 助手回复
     */
    @RequestMapping("/chatWithSystemMessage")
    public String chatWithSystemMessage(String userMessage) {
        return llmService.chatWithSystemMessage(userMessage);
    }

    /**
     * 带用户提示的聊天
     *
     * @param userMessage 用户输入的消息
     * @return 助手回复
     */
    @RequestMapping("/chatWithUserMessage")
    public String chatWithUserMessage(String userMessage) {
        return llmService.chatWithUserMessage(userMessage);
    }

    /**
     * 返回 Result 类型的聊天
     *
     * @param userMessage 用户输入的消息
     * @return 助手回复
     */
    @RequestMapping("/chatWithResult")
    public List<String> chatWithResult(String userMessage) {
        return llmService.chatWithResult(userMessage);
    }

    /**
     * 流式聊天
     *
     * @param userMessage 用户输入的消息
     * @return 助手回复
     */
    @RequestMapping(value = "/chatWithStreaming01", produces = "text/event-stream")
    public Flux<String> chatWithStreaming01(String userMessage) {
        return llmService.chatWithStreaming01(userMessage);
    }

    /**
     * 流式聊天02
     *
     * @param userMessage 用户输入的消息
     * @return 助手回复
     */
    @RequestMapping(value = "/chatWithStreaming02", produces = "text/event-stream")
    public Flux<String> chatWithStreaming02(String userMessage) {
        return llmService.chatWithStreaming02(userMessage);
    }

}