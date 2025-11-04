package com.kingfish.langchain4j.service.ai;

import com.kingfish.langchain4j.service.StructuredLlmServiceImpl;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;


@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "openAiChatModel")
public interface PersonExtractor {
    /**
     * 从文本中提取Person对象
     *
     * @param text 包含Person信息的文本
     * @return 提取到的Person对象
     */
    StructuredLlmServiceImpl.Person extractPersonFrom(String text);
}