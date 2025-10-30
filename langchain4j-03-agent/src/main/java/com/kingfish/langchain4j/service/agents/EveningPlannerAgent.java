package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.service.V;
import lombok.Data;

import java.util.List;

public interface EveningPlannerAgent {
    List<EveningPlan> plan(@V("mood") String mood);

    @Data
    class EveningPlan {
        private final String meal;
        private final String movie;
    }
}