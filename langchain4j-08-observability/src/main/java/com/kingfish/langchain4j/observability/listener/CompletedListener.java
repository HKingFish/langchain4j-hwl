package com.kingfish.langchain4j.observability.listener;

import dev.langchain4j.invocation.InvocationContext;
import dev.langchain4j.observability.api.event.AiServiceCompletedEvent;
import dev.langchain4j.observability.api.listener.AiServiceCompletedListener;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @Author : haowl
 * @Date : 2026/3/21 16:13
 * @Desc :
 */
@Slf4j
public class CompletedListener implements AiServiceCompletedListener {
    @Override
    public void onEvent(AiServiceCompletedEvent event) {
        InvocationContext invocationContext = event.invocationContext();
        Optional<Object> result = event.result();

        // 同一次 LLM 调用的 invocationId 是相同的
        UUID invocationId = invocationContext.invocationId();
        String interfaceName = invocationContext.interfaceName();
        String methodName = invocationContext.methodName();
        List<Object> methodArgs = invocationContext.methodArguments();
        Object chatMemoryId = invocationContext.chatMemoryId();
        Instant eventTimestamp = invocationContext.timestamp();

        log.info("[Completed][result = {},invocationId = {}, interfaceName = {}, methodName = {}," +
                        " methodArgs = {}, chatMemoryId = {}, eventTimestamp = {}]",
                result.get(), invocationId, interfaceName, methodName, methodArgs, chatMemoryId, eventTimestamp);
    }
}