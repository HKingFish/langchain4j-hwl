package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.UserMessage;

public interface TechnicalExpert {

    @UserMessage("""
                你是一名技术专家。
                请从技术角度分析以下用户请求，并提供尽可能好的答案。
                用户请求是{{request}}。
            """)
    @Tool("一名技术专家")
    String technical(String request);
}