package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @Author : haowl
 * @Date : 2025/10/30 21:01
 * @Desc : 查询余额代理类，提供查询用户账户余额的功能
 */
public interface BalanceAgent {

    @SystemMessage("""
            你是一名银行工作人员，查询指定用户的余额。
            """)
    @UserMessage("""
            查询{{user}}的账户余额，并返回余额。
            """)
    @Agent("查询指定用户的余额的银行工作人员")
    Double getBalance(@V("user") String user);
}


