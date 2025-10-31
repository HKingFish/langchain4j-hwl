package com.kingfish.langchain4j.service;

import com.kingfish.langchain4j.service.agents.*;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.agentic.supervisor.SupervisorAgent;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * @Author : haowl
 * @Date : 2025/10/29 9:11
 * @Desc :
 */
@Service
public class LlmServiceImpl implements LlmService {

    @Resource
    private CreativeWriter creativeWriter;

    @Resource(name = "storyAgent")
    private UntypedAgent storyAgent;

    @Resource(name = "eveningPlannerAgent")
    private EveningPlannerAgent eveningPlannerAgent;

    @Resource(name = "expertRouterAgent")
    private ExpertRouterAgent expertRouterAgent;

    @Resource(name = "eveningPlannerAgentByApi")
    private EveningPlannerAgentByApi eveningPlannerAgentByApi;

    @Resource(name = "expertRouterAgentWithMemory")
    private ExpertRouterAgentWithMemory expertRouterAgentWithMemory;

    @Resource(name = "bankAgent")
    private SupervisorAgent bankAgent;

    @Resource(name = "horoscopeAgent")
    private SupervisorAgent horoscopeAgent;



    @Override
    public String generateSimpleStory(String topic) {
        return creativeWriter.generateStory(topic);
    }

    @Override
    public String generateCompletedStory(String topic, String style, String audience) {
        Map<String, Object> input = Map.of(
                "topic", topic,
                "style", style,
                "audience", audience
        );
        return (String) storyAgent.invoke(input);
    }

    @Override
    public List<EveningPlannerAgent.EveningPlan> planEvening(String mood) {
        return eveningPlannerAgent.plan(mood);
    }

    @Override
    public List<EveningPlannerAgent.EveningPlan> planEveningByApi(String mood) {
        return eveningPlannerAgentByApi.plan(mood);
    }

    @Override
    public String askExpert(String request) {
        return expertRouterAgent.ask(request);
    }

    @Override
    public String askExpertWithHistory(String memoryId, String request) {
        return expertRouterAgentWithMemory.ask(memoryId, request);
    }

    @Override
    public String bankInvoke(String request) {
        return bankAgent.invoke(request);
    }


    @Override
    public String horoscopeInvoke(String request) {
        return horoscopeAgent.invoke(request);
    }
}
