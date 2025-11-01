package com.kingfish.langchain4j.service;

/**
 * @Author : haowl
 * @Date : 2025/10/29 9:11
 * @Desc :
 */
public interface RagLlmService {

    /**
     * 简单的问答
     *
     * @param userMessage 用户输入的消息
     * @return 模型返回的消息
     */
    String chat(String userMessage);


}
