package com.kingfish.langchain4j.service.agents;

import cn.hutool.extra.spring.SpringUtil;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.ChatModelSupplier;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

public interface FoodExpert {

    @UserMessage("""
                你是一位出色的晚间策划师。
                请根据给定的氛围提出3份餐食清单。
                氛围是{{mood}}。
                对于每份餐食，只需给出餐食的名称。
                提供包含3个项目的清单，除此之外不要有其他内容。
            """)
    @Agent
    List<String> findMeal(@V("mood") String mood);

    /**
     * 提供用于执行Agent的ChatModel
     *
     * @return
     */
    @ChatModelSupplier
    static ChatModel chatModel() {
        return SpringUtil.getBean(OpenAiChatModel.class);
    }
}