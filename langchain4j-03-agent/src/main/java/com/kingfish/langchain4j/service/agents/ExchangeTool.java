package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExchangeTool {

    @Tool("将指定金额的货币从原始货币兑换为目标货币")
    Double exchange(@P("originalCurrency") String originalCurrency, @P("amount") Double amount, @P("targetCurrency") String targetCurrency) {
        // Invoke a REST service to get the exchange rate

        if (originalCurrency.equals(targetCurrency)) {
            log.info("原始货币和目标货币相同，无需兑换");
            return amount;
        }
        if (originalCurrency.equals("CNY") && targetCurrency.equals("USD")) {
            log.info("将%s元兑换为%s美元，汇率为 0.7".formatted(amount, amount / 7.0));
            return amount / 7.0;
        }
        if (originalCurrency.equals("USD") && targetCurrency.equals("CNY")) {
            log.info("将%s美元兑换为%s元，汇率为 7".formatted(amount, amount * 7));
            return amount * 7;
        }

        throw new IllegalArgumentException("不支持的货币类型");
    }
}