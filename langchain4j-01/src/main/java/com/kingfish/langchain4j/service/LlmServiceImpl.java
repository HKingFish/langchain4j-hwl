package com.kingfish.langchain4j.service;

import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


/**
 * @Author : haowl
 * @Date : 2025/10/29 9:11
 * @Desc :
 */
@Service
public class LlmServiceImpl implements LlmService {

    @Resource
    private OpenAiChatModel chatModel;


    @Override
    public String chat(String userMessage) {
        return chatModel.chat(userMessage);
    }
}
