package com.kingfish.langchain4j.observability.controller;

import com.kingfish.langchain4j.observability.service.ObservabilityLlmService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author : haowl
 * @Date : 2025/10/28 20:53
 * @Desc :
 */
@RestController
@RequestMapping("/llm/observability")
public class ObservabilityLlmController {

    @Resource
    private ObservabilityLlmService llmService;


    @RequestMapping("/chat")
    public String chat(String prompt) {
        return llmService.chat(prompt);
    }
}