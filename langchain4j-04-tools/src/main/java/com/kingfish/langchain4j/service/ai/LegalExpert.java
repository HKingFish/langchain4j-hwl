package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.UserMessage;

public interface LegalExpert {

    @UserMessage("""
                你是一名法律专家。
                请从法律角度分析以下用户请求，并提供尽可能好的答案。
                用户请求是{{request}}。
            """)
    @Tool("一名法律专家")
    String legal(String request);
}