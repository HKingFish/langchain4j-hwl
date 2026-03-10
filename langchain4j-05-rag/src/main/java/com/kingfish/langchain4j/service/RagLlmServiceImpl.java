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

    //    @Resource
    private EasyRagAssistant easyRagAssistant;

    @Resource
    private CustomerServiceDispatcher customerServiceDispatcher;

    @Override
    public String easyChat(String userMessage) {
        return easyRagAssistant.chat(userMessage);
    }


    @Override
    public String customerServiceChat(String memoryId, String userMessage) {
        return customerServiceDispatcher.chat(memoryId, userMessage);
    }
}
