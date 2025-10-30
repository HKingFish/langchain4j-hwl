package com.kingfish.langchain4j.service;

import com.kingfish.langchain4j.service.agents.EveningPlannerAgent;

import java.util.List;

/**
 * @Author : haowl
 * @Date : 2025/10/29 9:11
 * @Desc :
 */
public interface LlmService {

    /**
     * 生成简单的故事
     *
     * @param topic 主题
     * @return 简单的故事
     */
    String generateSimpleStory(String topic);

    /**
     * 生成完整的故事
     *
     * @param topic    主题
     * @param style    风格
     * @param audience 受众
     * @return 完整的故事
     */
    String generateCompletedStory(String topic, String style, String audience);


    /**
     * 规划晚上的活动
     *
     * @param mood 心情
     * @return 晚上的活动计划
     */
    List<EveningPlannerAgent.EveningPlan> planEvening(String mood);

    /**
     * 规划晚上的活动（通过API）
     *
     * @param mood 心情
     * @return 晚上的活动计划
     */
    List<EveningPlannerAgent.EveningPlan> planEveningByApi(String mood);


    /**
     * 咨询专家
     *
     * @param request 咨询请求
     * @return 专家的回答
     */
    String askExpert(String request);

    /**
     * 咨询专家（包含历史记录）
     *
     * @param request 咨询请求
     * @return 专家的回答
     */
    String askExpertWithHistory(String memoryId, String request);
}
