package com.kingfish.langchain4j.observability.listener;

import dev.langchain4j.invocation.InvocationContext;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.observability.api.event.AiServiceRequestIssuedEvent;
import dev.langchain4j.observability.api.listener.AiServiceRequestIssuedListener;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * @Author : haowl
 * @Date : 2026/3/21 14:56
 * @Desc :
 */
@Slf4j
public class IssuedListener implements AiServiceRequestIssuedListener {

    @Override
    public void onEvent(AiServiceRequestIssuedEvent event) {
        InvocationContext invocationContext = event.invocationContext();
        ChatRequest request = event.request();

        // 同一次 LLM 调用的 invocationId 是相同的
        UUID invocationId = invocationContext.invocationId();
        String interfaceName = invocationContext.interfaceName();
        String methodName = invocationContext.methodName();
        List<Object> methodArgs = invocationContext.methodArguments();
        Object chatMemoryId = invocationContext.chatMemoryId();
        Instant eventTimestamp = invocationContext.timestamp();

        log.info("[RequestIssued][request = {}, invocationId = {}, interfaceName = {}, methodName = {}," +
                        " methodArgs = {}, chatMemoryId = {}, eventTimestamp = {}]",
                request.toString(), invocationId, interfaceName, methodName, methodArgs, chatMemoryId, eventTimestamp);
    }
}