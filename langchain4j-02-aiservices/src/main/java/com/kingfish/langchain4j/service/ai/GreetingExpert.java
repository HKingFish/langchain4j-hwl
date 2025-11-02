package com.kingfish.langchain4j.service.ai;

import dev.langchain4j.service.SystemMessage;

/**
 * @Author : haowl
 * @Date : 2025/11/2 10:06
 * @Desc :
 */
public interface GreetingExpert {
    /**
     * 是否是问候
     *
     * @param text 输入的文本
     * @return 是否是问候
     */
    @SystemMessage("判断用户输入是否是问候语")
    boolean isGreeting(String text);
}
