package com.kingfish.langchain4j.observability.listener;

import dev.langchain4j.observability.api.listener.AiServiceListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author : haowl
 * @Date : 2026/3/21 16:29
 * @Desc :
 */
@Slf4j
public class AiServiceCustomEventListener implements AiServiceListener<AiServiceCustomEvent> {
    @Override
    public Class<AiServiceCustomEvent> getEventClass() {
        return AiServiceCustomEvent.class;
    }

    @Override
    public void onEvent(AiServiceCustomEvent event) {
        log.info("[Custom Event][event = {}]", event);
    }
}