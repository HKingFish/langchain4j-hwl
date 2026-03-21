package com.kingfish.langchain4j.service.guardrail;

import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailRequest;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author : haowl
 * @Date : 2026/3/17 21:28
 * @Desc :
 */
@Component
@Slf4j
public class DemoOutputGuardrail implements OutputGuardrail {
    // 通用敏感词列表（基础违规内容）
    private static final List<String> SENSITIVE_WORDS = List.of("暴力", "色情", "赌博", "毒品", "诈骗", "违法");

    @Override
    public OutputGuardrailResult validate(OutputGuardrailRequest request) {
        String response = request.responseFromLLM().aiMessage().text();
        log.info("response: {}", response);
        // 敏感词校验
        for (String word : SENSITIVE_WORDS) {
            if (response.contains(word)) {
                return reprompt("携带敏感词", "请不要携带 %s 敏感词".formatted(word));
            }
        }

        // 无违规 → 返回合规结果（可附带轻微格式修正）
        String adjustedOutput = response.replaceAll("\\s+", " ");
        return OutputGuardrailResult.successWith(adjustedOutput);
    }
}