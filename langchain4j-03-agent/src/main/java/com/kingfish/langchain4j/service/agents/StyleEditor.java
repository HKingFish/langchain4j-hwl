package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface StyleEditor {

    @UserMessage("""
                你是一名专业编辑。
                分析并改写以下故事，使其更符合 {{style}} 风格，且连贯性更强。
                只返回故事，不要其他内容。
                故事内容为 {{story}}
            """)
    @Agent(name = "StyleEditor", description = "编辑故事以使其更符合给定的风格")
    String editStory(@V("story") String story, @V("style") String style);
}