package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.declarative.Output;
import dev.langchain4j.agentic.declarative.ParallelAgent;
import dev.langchain4j.agentic.declarative.ParallelExecutor;
import dev.langchain4j.agentic.declarative.SubAgent;
import dev.langchain4j.service.V;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @Author : haowl
 * @Date : 2025/10/30 13:47
 * @Desc :
 */
public interface EveningPlannerAgentByApi {
    @ParallelAgent(outputKey = "plans", subAgents = {
            @SubAgent(type = MovieExpert.class, outputKey = "meals"),
            @SubAgent(type = FoodExpert.class, outputKey = "movies")
    })
    List<EveningPlannerAgent.EveningPlan> plan(@V("mood") String mood);

    @ParallelExecutor
    static Executor executor() {
        return Executors.newFixedThreadPool(2);
    }

    @Output
    static List<EveningPlannerAgent.EveningPlan> createPlans(@V("movies") List<String> movies, @V("meals") List<String> meals) {
        List<EveningPlannerAgent.EveningPlan> moviesAndMeals = new ArrayList<>();
        for (int i = 0; i < movies.size(); i++) {
            if (i >= meals.size()) {
                break;
            }
            moviesAndMeals.add(new EveningPlannerAgent.EveningPlan(movies.get(i), meals.get(i)));
        }
        return moviesAndMeals;
    }
}