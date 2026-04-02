package com.kingfish.langchain4j.mcp.service.impl;

import com.kingfish.langchain4j.mcp.client.ai.MySqlLocalAssistant;
import com.kingfish.langchain4j.mcp.client.ai.MySqlRemoteAssistant;
import com.kingfish.langchain4j.mcp.client.ai.MySqlRemoteStreamAssistant;
import com.kingfish.langchain4j.mcp.service.McpService;
import dev.langchain4j.service.TokenStream;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @Author : haowl
 * @Date : 2026/3/30 21:12
 * @Desc :
 */
@Service
public class McpServiceImpl implements McpService {

    @Resource
    private MySqlLocalAssistant mySqlLocalAssistant;

    @Resource
    private MySqlRemoteAssistant mySqlRemoteAssistant;

    @Resource
    private MySqlRemoteStreamAssistant mySqlRemoteStreamAssistant;

    @Override
    public String localChat(String message) {
        return mySqlLocalAssistant.chat(message);
    }

    @Override
    public String remoteChat(String message) {
        return mySqlRemoteAssistant.chat(message);
    }

    @Override
    public Flux<String> remoteStreamChat(String message) {
        TokenStream tokenStream = mySqlRemoteStreamAssistant.chat(message);
        return Flux.create(sink ->
                tokenStream.onPartialResponse(sink::next)
                        .onCompleteResponse(response -> sink.complete())
                        .onError(sink::error)
                        .start()
        );
    }
}