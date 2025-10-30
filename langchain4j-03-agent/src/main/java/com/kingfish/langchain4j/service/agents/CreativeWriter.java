package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;


public interface CreativeWriter {
    @UserMessage("""
            你是一名创意作家。
            围绕给定主题生成一篇不超过3句话的故事草稿。
            只返回故事，其他什么都不要。
            主题是{{topic}}。
            """)
    @Agent(name = "CreativeWriter", description = "基于给定话题生成一篇不超过3句话的故事草稿", outputKey = "story")
    String generateStory(@V("topic") String topic);
}