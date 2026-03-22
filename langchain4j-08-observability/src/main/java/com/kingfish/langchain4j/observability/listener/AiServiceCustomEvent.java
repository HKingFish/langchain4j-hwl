package com.kingfish.langchain4j.observability.listener;

import dev.langchain4j.invocation.InvocationContext;
import dev.langchain4j.observability.api.event.AiServiceEvent;
import dev.langchain4j.observability.event.AbstractAiServiceEvent;

public interface AiServiceCustomEvent extends AiServiceEvent {

    @Override
    default Class<AiServiceCustomEvent> eventClass() {
        return AiServiceCustomEvent.class;
    }

    @Override
    default AiServiceCustomEventBuilder toBuilder() {
        return new AiServiceCustomEventBuilder(this);
    }

    static AiServiceCustomEventBuilder builder() {
        return new AiServiceCustomEventBuilder();
    }

    static AiServiceCustomEventBuilder builder(AiServiceCustomEvent src) {
        return new AiServiceCustomEventBuilder(src);
    }

    class AiServiceCustomEventBuilder extends Builder<AiServiceCustomEvent> {

        private String sensitiveWord;

        protected AiServiceCustomEventBuilder() {
        }

        protected AiServiceCustomEventBuilder(AiServiceCustomEvent src) {
            super(src);
        }

        public AiServiceCustomEventBuilder invocationContext(InvocationContext invocationContext) {
            return (AiServiceCustomEventBuilder) super.invocationContext(invocationContext);
        }

        public String sensitiveWord() {
            return sensitiveWord;
        }

        public AiServiceCustomEventBuilder sensitiveWord(String sensitiveWord) {
            this.sensitiveWord = sensitiveWord;
            return this;
        }

        @Override
        public AiServiceCustomEvent build() {
            return new DefaultAiServiceCustomEvent(this);
        }
    }


    class DefaultAiServiceCustomEvent extends AbstractAiServiceEvent implements AiServiceCustomEvent {
        protected DefaultAiServiceCustomEvent(AiServiceCustomEventBuilder builder) {
            super(builder);
        }
    }
}
