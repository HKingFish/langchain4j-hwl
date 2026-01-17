package com.kingfish.langchain4j.controller;

import com.kingfish.langchain4j.service.AgentLlmService;
import com.kingfish.langchain4j.service.agents.EveningPlannerAgent;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @Author : haowl
 * @Date : 2025/10/28 20:53
 * @Desc :
 */
@RestController
@RequestMapping("/llm/agent")
public class AgentLlmController {

    @Resource
    private AgentLlmService llmService;


    @RequestMapping("/generateSimpleStory")
    public String generateSimpleStory(String topic) {
        return llmService.generateSimpleStory(topic);
    }

    @RequestMapping("/generateCompletedStory")
    public String generateCompletedStory(String topic, String style, String audience) {
        return llmService.generateCompletedStory(topic, style, audience);
    }

    @RequestMapping("/planEvening")
    public List<EveningPlannerAgent.EveningPlan> planEvening(String mood) {
        return llmService.planEvening(mood);
    }

    @RequestMapping("/planEveningByApi")
    public List<EveningPlannerAgent.EveningPlan> planEveningByApi(String mood) {
        return llmService.planEveningByApi(mood);
    }

    @RequestMapping("/askExpert")
    public String askExpert(String request) {
        return llmService.askExpert(request);
    }

    @RequestMapping("/askExpertWithHistory")
    public String askExpertWithHistory(String memoryId, String request) {
        return llmService.askExpertWithHistory(memoryId, request);
    }

    /**
     * 银行.invoke
     * - 将 100 人民币从张三账户转到李四账户
     * - 查询张三的账户余额
     * - 将 50 美元从李四账户提取到张三账户
     *
     * @param request 银行请求
     * @return 银行的回答
     */
    @RequestMapping("/bankInvoke")
    public String bankInvoke(String request) {
        return llmService.bankInvoke(request);
    }

    /**
     * horoscope.invoke
     * - 我叫张三。我的星座运势是什么？
     *
     * @param request horoscope请求
     * @return horoscope的回答
     */
    @RequestMapping("/horoscopeInvoke")
    public String horoscopeInvoke(String request) {
        return llmService.horoscopeInvoke(request);
    }
}