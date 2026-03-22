package com.kingfish.langchain4j.observability.guardrail;

import com.kingfish.langchain4j.observability.listener.AiServiceCustomEvent;
import dev.langchain4j.guardrail.GuardrailRequestParams;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailRequest;
import dev.langchain4j.guardrail.InputGuardrailResult;
import dev.langchain4j.invocation.InvocationContext;
import dev.langchain4j.observability.api.AiServiceListenerRegistrar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author : haowl
 * @Date : 2026/3/21 9:16
 * @Desc :
 */
@Component
@Slf4j
public class DemoInputGuardrail implements InputGuardrail {
    /**
     * 敏感词列表（业务自定义）
     */
    private static final List<String> SENSITIVE_WORDS = List.of("暴力", "色情", "赌博", "毒品", "诈骗");

    @Override
    public InputGuardrailResult validate(InputGuardrailRequest request) {
        String prompt = request.userMessage().singleText();
        GuardrailRequestParams requestParams = request.requestParams();
        InvocationContext invocationContext = requestParams.invocationContext();
        AiServiceListenerRegistrar aiservicelistenerregistrar = requestParams.aiservicelistenerregistrar();
        log.info("prompt: {}", prompt);

        // 敏感词校验
        for (String sensitiveWord : SENSITIVE_WORDS) {
            if (prompt.contains(sensitiveWord)) {
                // 敏感词触发 AiServiceCustomEvent
                aiservicelistenerregistrar.fireEvent(
                        AiServiceCustomEvent.builder()
                                .invocationContext(invocationContext)
                                .sensitiveWord(sensitiveWord)
                                .build());
                return fatal("存在输入敏感词");
            }
        }

        return InputGuardrailResult.success();
    }
}