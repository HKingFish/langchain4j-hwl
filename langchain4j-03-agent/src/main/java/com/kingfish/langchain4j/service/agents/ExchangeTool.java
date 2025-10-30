package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

public class ExchangeTool {

    @Tool("将指定金额的货币从原始货币兑换为目标货币")
    Double exchange(@P("originalCurrency") String originalCurrency, @P("amount") Double amount, @P("targetCurrency") String targetCurrency) {
        // Invoke a REST service to get the exchange rate

        if (originalCurrency.equals(targetCurrency)) {
            return amount;
        }
        if (originalCurrency.equals("CNY")) {
            return amount * 0.15;
        }
        if (originalCurrency.equals("USD")) {
            return amount;
        }

        throw new IllegalArgumentException("不支持的货币类型");
    }
}