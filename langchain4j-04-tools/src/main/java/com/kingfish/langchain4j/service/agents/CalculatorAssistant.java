package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.service.Result;

/**
 * @Author : haowl
 * @Date : 2025/11/1 15:46
 * @Desc :
 */
public interface CalculatorAssistant {
    // 返回类型需要是 Result
    Result<String> chat(String userMessage);
}
