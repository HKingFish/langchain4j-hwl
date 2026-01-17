package com.kingfish.langchain4j.controller;

import com.kingfish.langchain4j.service.ToolsLlmService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


/**
 * @Author : haowl
 * @Date : 2025/10/28 20:53
 * @Desc :
 */
@RestController
@RequestMapping("/llm/tools")
public class ToolsLlmController {

    @Resource
    private ToolsLlmService llmService;

    /**
     * 简单的问答
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    @RequestMapping("/chatByLowLevelApi")
    public String chatByLowLevelApi(String userMessage) {
        return llmService.chatByLowLevelApi(userMessage);
    }

    /**
     * 简单的问答 - high level api
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    @RequestMapping("/chatByHighLevelApi")
    public String chatByHighLevelApi(String userMessage) {
        return llmService.chatByHighLevelApi(userMessage);
    }

    /**
     * 问答 - 路由到不同的专家
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    @RequestMapping("/chatWithExpert")
    public String chatWithExpert(String userMessage) {
        return llmService.chatWithExpert(userMessage);
    }

    /**
     * 问答 - 获取书籍详情
     *
     * @param bookName 书籍名称
     * @return 书籍详情
     */
    @RequestMapping("/bookDetail")
    public String bookDetail(String bookName) {
        return llmService.bookDetail(bookName);
    }

    /**
     * 问答 - 动态的调用不同的工具
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    @RequestMapping("/dynamicChat")
    public String dynamicChat(String userMessage) {
        return llmService.dynamicChat(userMessage);
    }

    /**
     * 问答 - 计算器
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    @RequestMapping("/calculator")
    public String calculator(String userMessage) {
        return llmService.calculator(userMessage);
    }

    /**
     * 问答 - 计算器
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    @RequestMapping(value = "/calculatorStream", produces = "text/event-stream")
    public Flux<String> calculatorStream(String userMessage) {
        return llmService.calculatorStream(userMessage);
    }
}