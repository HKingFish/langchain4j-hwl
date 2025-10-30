package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;


public interface MedicalExpertWithMemory {

    @UserMessage("""
                你是一名医学专家。
                请从医学角度分析以下用户请求，并提供尽可能好的答案。
                用户请求是{{request}}。
            """)
    @Agent("一名医学专家")
    String medical(@MemoryId String memoryId, @V("request") String request);
}