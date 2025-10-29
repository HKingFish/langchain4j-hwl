package com.kingfish.langchain4j.controller;

import com.kingfish.langchain4j.service.LlmService;
import dev.langchain4j.data.message.AiMessage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;


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
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    @RequestMapping("/chat")
    public String chat(String userMessage) {
        return llmService.chat(userMessage);
    }

     /**
     * 带历史记录的问答
     * @param userMessage 用户输入的消息 （我叫张三\n我叫什么名字）
     * @return 模型返回的消息
     */
    @RequestMapping("/chatWithHistory")
    public String chatWithHistory(String userMessage) {
        return llmService.chatWithHistory(userMessage).text();
    }

     /**
     * 多模态问答
     * @param userMessage 用户输入的消息 （图片中是什么动物）
     * @return 模型返回的消息
     */
    @RequestMapping("/chatWithMultimodality")
    public String chatWithMultimodality(String userMessage) throws IOException {
        return llmService.chatWithMultimodality(userMessage).text();
    }

     /**
     * 带持久化历史记录的问答 - low level
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    @RequestMapping("/chatWithLowLevelPersistentHistory")
    public String chatWithLowLevelPersistentHistory(String memoryId, String userMessage) {
        return llmService.chatWithLowLevelPersistentHistory(memoryId, userMessage).text();
    }

     /**
     * 带持久化历史记录的问答 - high level
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    @RequestMapping("/chatWithHighLevelPersistentHistory")
    public String chatWithHighLevelPersistentHistory(String memoryId, String userMessage) {
        return llmService.chatWithHighLevelPersistentHistory(memoryId, userMessage);
    }


     /**
     * 流式问答
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    @RequestMapping(value = "/streamChat", produces = "text/html;charset=utf-8")
    public Flux<String> streamChat(String userMessage) {
        return llmService.streamChat(userMessage);
    }

}