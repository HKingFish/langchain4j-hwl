package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AudienceEditor {

    @UserMessage("""
                你是一名专业编辑。
                分析并改写以下故事，使其更贴合{{audience}}这一目标受众。
                仅返回故事本身，不要其他内容。
                故事内容为“{{story}}”。".
            """)
    @Agent(name = "AudienceEditor", description = "修改一个故事，使其更适合特定的受众")
    String editStory(@V("story") String story, @V("audience") String audience);
}