package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface StyleScorer {

    @UserMessage("""
            你是一名评论家。
            请根据以下故事与“{{style}}”风格的契合程度，为其给出0.0到1.0之间的评分。
            只返回分数，不要其他内容。
            故事内容为：“{{story}}”
            """)
    @Agent("根据故事与给定风格的匹配程度为其打分")
    double scoreStyle(@V("story") String story, @V("style") String style);
}