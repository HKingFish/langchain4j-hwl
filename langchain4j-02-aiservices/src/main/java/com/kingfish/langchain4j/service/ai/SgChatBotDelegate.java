package com.kingfish.langchain4j.service.ai;

/**
 * @Author : haowl
 * @Date : 2025/11/2 10:08
 * @Desc :
 */
public class SgChatBotDelegate {

    /**
     * 问候专家
     */
    private final GreetingExpert greetingExpert;

    /**
     * 聊天机器人
     */
    private final SgChatBot chatBot;

    public SgChatBotDelegate(GreetingExpert greetingExpert, SgChatBot chatBot) {
        this.greetingExpert = greetingExpert;
        this.chatBot = chatBot;
    }


    /**
     * 聊天
     *
     * @param userMessage 用户输入的消息
     * @return 助手回复
     */
    public String chat(String userMessage) {
        if (greetingExpert.isGreeting(userMessage)) {
            return "你好！我是三国野史机器人";
        }
        return chatBot.chat(userMessage);
    }


}
