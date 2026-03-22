package com.kingfish.langchain4j.observability.service.impl;

import com.kingfish.langchain4j.observability.service.ChatAssistant;
import com.kingfish.langchain4j.observability.service.ObservabilityLlmService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author : haowl
 * @Date : 2026/3/21 10:53
 * @Desc :
 */
@Slf4j
@Service
public class ObservabilityLlmServiceImpl implements ObservabilityLlmService {

    @Resource
    private ChatAssistant chatAssistant;

    @Override
    public String chat(String prompt) {
        return chatAssistant.chat(prompt);
    }
}