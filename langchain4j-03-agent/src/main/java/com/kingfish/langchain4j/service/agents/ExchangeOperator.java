package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.V;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExchangeOperator {

    @Agent(value = "一个货币兑换器，可将给定金额的货币从原始货币转换为目标货币",
            outputKey = "exchange")
    public Double exchange(@V("originalCurrency") String originalCurrency, @V("amount") Double amount, @V("targetCurrency") String targetCurrency) {
        if (originalCurrency.equals(targetCurrency)) {
            log.info("原始货币和目标货币相同，无需兑换");
            return amount;
        }
        if (originalCurrency.equals("CNY") && targetCurrency.equals("USD")) {
            log.info("将%s元兑换为%s美元，汇率为0.15".formatted(amount, amount * 0.15));
            return amount * 0.15;
        }
        if (originalCurrency.equals("USD") && targetCurrency.equals("CNY")) {
            log.info("将%s美元兑换为%s元，汇率为 7".formatted(amount, amount * 7));
            return amount * 7;
        }

        throw new IllegalArgumentException("不支持的货币类型");
    }
}