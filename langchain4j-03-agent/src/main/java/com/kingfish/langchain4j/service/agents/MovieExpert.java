package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

public interface MovieExpert {

    @UserMessage("""
                你是一位出色的晚间规划师。
                根据给定的情绪，推荐3部匹配的电影。
                情绪是{{mood}}。
                只需提供包含这3部电影的列表，其他内容无需提供。
            """)
    @Agent
    List<String> findMovie(@V("mood") String mood);
}