package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.service.TokenStream;

/**
 * @Author : haowl
 * @Date : 2025/11/1 15:46
 * @Desc :
 */
public interface StreamCalculatorAssistant {
    // 返回类型是 TokenStream
    TokenStream chat(String userMessage);
}
