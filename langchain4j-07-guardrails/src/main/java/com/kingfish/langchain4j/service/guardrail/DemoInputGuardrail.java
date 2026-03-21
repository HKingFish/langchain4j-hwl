package com.kingfish.langchain4j.service.guardrail;

import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailRequest;
import dev.langchain4j.guardrail.InputGuardrailResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
     * 最大输入长度
     */
    private static final int MAX_INPUT_LENGTH = 200;
    /**
     * 敏感词列表（业务自定义）
     */
    private static final List<String> SENSITIVE_WORDS = List.of("暴力", "色情", "赌博", "毒品", "诈骗");

    @Override
    public InputGuardrailResult validate(InputGuardrailRequest request) {
        String prompt = request.userMessage().singleText();
        log.info("prompt: {}", prompt);
        // 1. 非空校验
        if (StringUtils.isBlank(prompt)) {
            return fatal("输入内容不能为空");
        }

        // 2. 长度校验
        if (prompt.length() > MAX_INPUT_LENGTH) {
            return fatal("输入长度超过最大限制");
        }

        // 3. 敏感词校验
        for (String sensitiveWord : SENSITIVE_WORDS) {
            if (prompt.contains(sensitiveWord)) {
                return fatal("存在输入敏感词");
            }
        }

        // 4. 格式化修正输入词
        String adjustedText = formatInputText(prompt);

        // 5. 返回「合规+修正后内容」的结果（使用 successWith）
        return InputGuardrailResult.successWith(adjustedText);
    }

    /**
     * 辅助方法：标准化输入文本格式
     *
     * @param originalText 原始输入文本
     * @return 修正后的文本
     */
    private String formatInputText(String originalText) {
        // 规则1：替换多个连续换行/空格为单个空格
        String formatted = originalText.replaceAll("\\s+", " ");
        // 规则2：如果输入以问号结尾但无前缀，补充「用户提问：」前缀
        if (formatted.endsWith("?") || formatted.endsWith("？")) {
            if (!formatted.startsWith("用户提问：")) {
                formatted = "用户提问：" + formatted;
            }
        }
        // 规则3：去除首尾特殊字符（如！、@、#等）
        formatted = formatted.replaceAll("^[^a-zA-Z0-9\u4e00-\u9fa5]+", "")
                .replaceAll("[^a-zA-Z0-9\u4e00-\u9fa5]+$", "");
        return formatted;
    }

}