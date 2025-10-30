package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @Author : haowl
 * @Date : 2025/10/30 21:01
 * @Desc : 取款代理类，提供从用户账户中提取美元的功能
 */
public interface WithdrawAgent {

    @SystemMessage("""
            你是一名银行工作人员，只能从用户账户中提取美元（USD）。
            """)
    @UserMessage("""
            从{{user}}的账户中提取{{amount}}美元，并返回新的余额。
            """)
    @Agent("从账户中提取美元的银行工作人员")
    String withdraw(@V("user") String user, @V("amount") Double amount);
}


