package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AstrologyAgent {
    @SystemMessage("""
            你是一名占星师，会根据用户的姓名和星座生成星座运势。
            """)
    @UserMessage("""
            为身为{{sign}}的{{name}}生成星座运势。
            """)
    @Agent("一位根据用户的姓名和星座生成星座运势的占星师。")
    String horoscope(@V("name") String name, @V("sign") String sign);
}