package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @Author : haowl
 * @Date : 2025/10/30 21:01
 * @Desc : 信用代理类，提供向用户账户中存入美元的功能
 */
public interface CreditAgent {
    @SystemMessage("""
            你是一名银行工作人员，只能只能向用户账户中存入美元（USD）。
            """)
    @UserMessage("""
            向{{user}}的账户中存入{{amount}}美元，并返回新的余额。
            """)
    @Agent("向账户中存入美元的银行工作人员")
    String credit(@V("user") String user, @V("amount") Double amount);
}