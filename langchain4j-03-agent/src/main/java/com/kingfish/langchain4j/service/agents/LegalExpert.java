package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface LegalExpert {

    @UserMessage("""
                你是一名法律专家。
                请从法律角度分析以下用户请求，并提供尽可能好的答案。
                用户请求是{{request}}。
            """)
    @Agent("一名法律专家")
    String legal(@V("request") String request);
}