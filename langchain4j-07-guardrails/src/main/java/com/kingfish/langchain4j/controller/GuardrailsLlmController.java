package com.kingfish.langchain4j.controller;

import com.kingfish.langchain4j.service.GuardrailsLlmService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author : haowl
 * @Date : 2025/10/28 20:53
 * @Desc :
 */
@RestController
@RequestMapping("/llm/guardrails")
public class GuardrailsLlmController {

    @Resource
    private GuardrailsLlmService llmService;


    @RequestMapping("/chat")
    public String chat(String prompt) {
        return llmService.chat(prompt);
    }
}