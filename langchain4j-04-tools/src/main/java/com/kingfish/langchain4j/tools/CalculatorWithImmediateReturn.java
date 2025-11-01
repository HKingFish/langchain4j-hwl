package com.kingfish.langchain4j.tools;

import dev.langchain4j.agent.tool.ReturnBehavior;
import dev.langchain4j.agent.tool.Tool;

public class CalculatorWithImmediateReturn {

    @Tool(returnBehavior = ReturnBehavior.IMMEDIATE)
    double add(int a, int b) {
        return a + b;
    }
}