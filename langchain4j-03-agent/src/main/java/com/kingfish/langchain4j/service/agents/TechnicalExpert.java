package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface TechnicalExpert {

    @UserMessage("""
                你是一名技术专家。
                请从技术角度分析以下用户请求，并提供尽可能好的答案。
                用户请求是{{request}}。
            """)
    @Agent("一名技术专家")
    String technical(@V("request") String request);
}