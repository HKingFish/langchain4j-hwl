package com.kingfish.langchain4j.service;

import reactor.core.publisher.Flux;

/**
 * @Author : haowl
 * @Date : 2025/10/29 9:11
 * @Desc :
 */
public interface ToolsLlmService {


    /**
     * 简单的问答 - low level
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    String chatByLowLevelApi(String userMessage);


    /**
     * 简单的问答 - high level
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    String chatByHighLevelApi(String userMessage);


    /**
     * 问答 - 路由到不同的专家
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    String chatWithExpert(String userMessage);


    /**
     * 获取书籍详情
     *
     * @param bookName 书籍名称
     * @return 书籍详情
     */
    String bookDetail(String bookName);


    /**
     * 动态的问答 - 可以根据用户输入的消息，动态的调用不同的工具
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    String dynamicChat(String userMessage);

    /**
     * 问答 - 计算器
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    String calculator(String userMessage);

    /**
     * 流式计算
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    Flux<String> calculatorStream(String userMessage);
}
