package com.kingfish.langchain4j.service;

import com.kingfish.langchain4j.service.ai.EasyRagAssistant;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


/**
 * @Author : haowl
 * @Date : 2025/10/29 9:11
 * @Desc :
 */
@Service
public class RagLlmServiceImpl implements RagLlmService {

    @Resource
    private EasyRagAssistant easyRagAssistant;

    @Override
    public String chat(String userMessage) {
        return easyRagAssistant.chat(userMessage);
    }
}
