package com.kingfish.langchain4j.service.guardrail;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.GuardrailRequestParams;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailRequest;
import dev.langchain4j.guardrail.InputGuardrailResult;

/**
 * @Author : haowl
 * @Date : 2026/3/17 21:25
 * @Desc :
 */
public class FirstInputGuardrail implements InputGuardrail {

    @Override
    public InputGuardrailResult validate(InputGuardrailRequest request) {
        UserMessage userMessage = request.userMessage();
        GuardrailRequestParams guardrailRequestParams = request.requestParams();
        return InputGuardrailResult.success();
    }
}