package com.kingfish.langchain4j.service.guardrail;

import dev.langchain4j.guardrail.*;
import dev.langchain4j.model.chat.response.ChatResponse;

/**
 * @Author : haowl
 * @Date : 2026/3/17 21:28
 * @Desc :
 */
public class FirstOutputGuardrail implements OutputGuardrail {

    @Override
    public OutputGuardrailResult validate(OutputGuardrailRequest request) {
        ChatExecutor chatExecutor = request.chatExecutor();
        ChatResponse chatResponse = request.responseFromLLM();
        GuardrailRequestParams guardrailRequestParams = request.requestParams();

        return OutputGuardrailResult.success();
    }
}