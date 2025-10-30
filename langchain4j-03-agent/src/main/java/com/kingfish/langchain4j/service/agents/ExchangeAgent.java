package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @Author : haowl
 * @Date : 2025/10/30 21:01
 * @Desc : 兑换代理类，提供将货币兑换为目标货币的功能
 */
public interface ExchangeAgent {
    @UserMessage("""
            你是一名名货币兑换操作员。
            使用工具将{{amount}}{{originalCurrency}}兑换为{{targetCurrency}}，
            仅返回工具提供的最终金额，不添加其他内容。
            """)
    @Agent("将指定金额的货币从原始货币兑换为目标货币的兑换员")
    Double exchange(@V("originalCurrency") String originalCurrency, @V("amount") Double amount, @V("targetCurrency") String targetCurrency);
}