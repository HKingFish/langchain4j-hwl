package com.kingfish.langchain4j.service.impl;

import com.kingfish.langchain4j.service.GuardrailsLlmService;
import com.kingfish.langchain4j.service.ai.ChatAssistant;
import dev.langchain4j.guardrail.InputGuardrailException;
import dev.langchain4j.guardrail.OutputGuardrailException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @Author : haowl
 * @Date : 2026/3/16 21:23
 * @Desc :
 */
@Service
public class GuardrailsLlmServiceImpl implements GuardrailsLlmService {

    @Resource
    private ChatAssistant chatAssistant;

    @Override
    public String chat(String prompt) {
        try {
            return chatAssistant.chat(prompt);
        } catch (InputGuardrailException | OutputGuardrailException e) {
            return e.getMessage();
        }
    }
}