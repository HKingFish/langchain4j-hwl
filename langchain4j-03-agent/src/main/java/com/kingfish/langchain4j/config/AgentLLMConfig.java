package com.kingfish.langchain4j.config;

import com.kingfish.langchain4j.service.agents.*;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.agentic.agent.AgentRequest;
import dev.langchain4j.agentic.agent.AgentResponse;
import dev.langchain4j.agentic.supervisor.SupervisorAgent;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.agentic.workflow.HumanInTheLoop;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;

/**
 * @Author : haowl
 * @Date : 2025/10/29 9:40
 * @Desc :
 */
@Slf4j
@Configuration
public class AgentLLMConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactor) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactor);
        //设置key序列化方式string
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置value的序列化方式json，使用GenericJackson2JsonRedisSerializer替换默认序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    /**
     * OpenAi模型
     *
     * @return
     * @link <a href="https://docs.langchain4j.dev/get-started/">...</a>
     */
    @Bean
    @Primary
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();
    }

    @Bean("aliQwenModel")
    public ChatModel ImageModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("aliQwen-api"))
                .modelName("qwen2.5-omni-7b")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .build();
    }


    @Bean("aliQwenStreamingChatModel")
    public OpenAiStreamingChatModel aliQwenStreamingChatModel() {
        return OpenAiStreamingChatModel
                .builder()
                .apiKey(System.getenv("aliQwen-api"))
                .modelName("qwen2.5-omni-7b")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .build();
    }


    @Bean
    public ChatMemoryProvider chatMemoryProvider(RedisChatMemoryStore chatMemoryStore) {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(20)
                .chatMemoryStore(chatMemoryStore)
                .build();
    }

    @Bean
    public CreativeWriter creativeWriter(OpenAiChatModel openAiChatModel) {
        return AgenticServices
                .agentBuilder(CreativeWriter.class)
                .afterAgentInvocation(this::log)
                .chatModel(openAiChatModel)
                .outputKey("story")
                .build();
    }

    @Bean
    public AudienceEditor audienceEditor(OpenAiChatModel openAiChatModel) {
        return AgenticServices
                .agentBuilder(AudienceEditor.class)
                .afterAgentInvocation(this::log)
                .chatModel(openAiChatModel)
                .outputKey("story")
                .build();
    }

    @Bean
    public StyleEditor styleEditor(OpenAiChatModel openAiChatModel) {
        return AgenticServices
                .agentBuilder(StyleEditor.class)
                .afterAgentInvocation(this::log)
                .chatModel(openAiChatModel)
                .outputKey("story")
                .build();
    }

    @Bean
    public StyleScorer styleScorer(OpenAiChatModel openAiChatModel) {
        return AgenticServices
                .agentBuilder(StyleScorer.class)
                .afterAgentInvocation(this::log)
                .chatModel(openAiChatModel)
                .outputKey("score")
                .build();
    }


    @Bean("styleReviewLoopAgent")
    public UntypedAgent styleReviewLoopAgent(StyleScorer styleScorer, StyleEditor styleEditor) {
        return AgenticServices
                .loopBuilder()
                .subAgents(styleScorer, styleEditor)
                .maxIterations(5)
                .afterAgentInvocation(this::log)
                // 设置仅在循环结束时检查退出条件，从而强制在测试该条件之前调用所有智能体
                .testExitAtLoopEnd(true)
                .exitCondition((agenticScope, loopCounter) -> {
                    // 如果循环次数小于等于3，评分大于等于0.8则退出
                    // 如果循环次数大于3，评分大于等于0.6则退出
                    double score = agenticScope.readState("score", 0.0);
                    return loopCounter <= 3 ? score >= 0.8 : score >= 0.6;
                })
                .build();
    }

    @Bean("storyAgent")
    public UntypedAgent untypedAgent(CreativeWriter creativeWriter, AudienceEditor audienceEditor,
                                     @Qualifier("styleReviewLoopAgent") UntypedAgent styleReviewLoopAgent) {
        return AgenticServices
                .sequenceBuilder()
                .subAgents(creativeWriter, audienceEditor, styleReviewLoopAgent)
                // 异常处理
//                .errorHandler(new Function<ErrorContext, ErrorRecoveryResult>() {
//                    @Override
//                    public ErrorRecoveryResult apply(ErrorContext errorContext) {
//                        if (errorContext.agentName().equals("generateStory") &&
//                                errorContext.exception() instanceof MissingArgumentException mEx && mEx.argumentName().equals("topic")) {
//                            errorContext.agenticScope().writeState("topic", "dragons and wizards");
//                            return ErrorRecoveryResult.retry();
//                        }
//                        return ErrorRecoveryResult.throwException();
//                    }
//                })
                .outputKey("story")
                .build();
    }

    @Bean("novelCreator")
    public NovelCreator novelCreator(CreativeWriter creativeWriter, AudienceEditor audienceEditor, StyleEditor styleEditor) {
        return AgenticServices
                .sequenceBuilder(NovelCreator.class)
                .subAgents(creativeWriter, audienceEditor, styleEditor)
                .outputKey("story")
                .build();
    }

    private void log(AgentRequest request) {
        log.info("AgentRequest: {}: {}: {}", request.agentName(), request.inputs(), request.agenticScope());
    }

    private void log(AgentResponse response) {
        log.info("AgentResponse: {}: {}", response.agentName(), response.output());
    }


    @Bean
    public MovieExpert movieExpert(OpenAiChatModel openAiChatModel) {
        return AgenticServices
                .agentBuilder(MovieExpert.class)
                .afterAgentInvocation(this::log)
                .chatModel(openAiChatModel)
                .outputKey("movies")
                .build();
    }

    @Bean
    public FoodExpert foodExpert(OpenAiChatModel openAiChatModel) {
        return AgenticServices
                .agentBuilder(FoodExpert.class)
                .afterAgentInvocation(this::log)
                .chatModel(openAiChatModel)
                .outputKey("meals")
                .build();
    }

    @Bean
    public EveningPlannerAgentByApi eveningPlannerAgentByApi(OpenAiChatModel chatModel) {
        // 这里使用 createAgenticSystem 来创建
        return AgenticServices
                .createAgenticSystem(EveningPlannerAgentByApi.class, chatModel);
    }

    @Bean
    public EveningPlannerAgent eveningPlannerAgent(FoodExpert foodExpert, MovieExpert movieExpert) {
        return AgenticServices
                .parallelBuilder(EveningPlannerAgent.class)
                .subAgents(foodExpert, movieExpert)
                // 并行执行，最多2个线程
                .executor(Executors.newFixedThreadPool(2))
                .outputKey("plans")
                .output(agenticScope -> {
                    List<String> movies = agenticScope.readState("movies", List.of());
                    List<String> meals = agenticScope.readState("meals", List.of());

                    List<EveningPlannerAgent.EveningPlan> moviesAndMeals = new ArrayList<>();
                    for (int i = 0; i < movies.size(); i++) {
                        if (i >= meals.size()) {
                            break;
                        }
                        moviesAndMeals.add(new EveningPlannerAgent.EveningPlan(movies.get(i), meals.get(i)));
                    }
                    return moviesAndMeals;
                })
                .build();
    }

    @Bean
    public MedicalExpert medicalExpert(OpenAiChatModel openAiChatModel) {
        return AgenticServices
                .agentBuilder(MedicalExpert.class)
                .afterAgentInvocation(this::log)
                .chatModel(openAiChatModel)
                .outputKey("response")
                .build();
    }

    @Bean
    public LegalExpert legalExpert(OpenAiChatModel openAiChatModel) {
        return AgenticServices
                .agentBuilder(LegalExpert.class)
                .afterAgentInvocation(this::log)
                .chatModel(openAiChatModel)
                .outputKey("response")
                .build();
    }

    @Bean
    public TechnicalExpert technicalExpert(OpenAiChatModel openAiChatModel) {
        return AgenticServices
                .agentBuilder(TechnicalExpert.class)
                .afterAgentInvocation(this::log)
                .chatModel(openAiChatModel)
                .outputKey("response")
                .build();
    }

    @Bean
    public CategoryRouter agenticServices(OpenAiChatModel openAiChatModel) {
        return AgenticServices
                .agentBuilder(CategoryRouter.class)
                .afterAgentInvocation(this::log)
                .chatModel(openAiChatModel)
                .outputKey("category")
                .build();
    }

    @Bean
    public ExpertRouterAgent expertRouterAgent(CategoryRouter categoryRouter, MedicalExpert medicalExpert,
                                               LegalExpert legalExpert, TechnicalExpert technicalExpert) {
        // 构建一个条件工作流 ： 根据分类路由到不同的专家
        UntypedAgent expertsAgent = AgenticServices.conditionalBuilder()
                .subAgents(agenticScope -> agenticScope.readState("category",
                        CategoryRouter.RequestCategory.UNKNOWN) == CategoryRouter.RequestCategory.MEDICAL, medicalExpert)
                .subAgents(agenticScope -> agenticScope.readState("category",
                        CategoryRouter.RequestCategory.UNKNOWN) == CategoryRouter.RequestCategory.LEGAL, legalExpert)
                .subAgents(agenticScope -> agenticScope.readState("category",
                        CategoryRouter.RequestCategory.UNKNOWN) == CategoryRouter.RequestCategory.TECHNICAL, technicalExpert)
                .build();

        // 构建一个顺序工作流 : 先通过 categoryRouter 对问题进行分类分类，再路由到不同的专家 Agent 进行处理
        return AgenticServices
                .sequenceBuilder(ExpertRouterAgent.class)
                .subAgents(categoryRouter, expertsAgent)
                .outputKey("response")
                .build();
    }


    /*************************************************************************/


    @Bean
    public CategoryRouterWithMemory categoryRouterWithMemory(ChatMemoryProvider chatMemoryProvider, @Qualifier("aliQwenModel") ChatModel chatModel) {
        return AgenticServices
                .agentBuilder(CategoryRouterWithMemory.class)
                .beforeAgentInvocation(this::log)
                .afterAgentInvocation(this::log)
                .chatModel(chatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .outputKey("category")
                .build();
    }

    @Bean
    public LegalExpertWithMemory legalExpertWithMemory(ChatMemoryProvider chatMemoryProvider, @Qualifier("aliQwenModel") ChatModel chatModel) {
        return AgenticServices
                .agentBuilder(LegalExpertWithMemory.class)
                .beforeAgentInvocation(this::log)
                .afterAgentInvocation(this::log)
                .chatModel(chatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .summarizedContext("medical", "technical")
                .outputKey("response")
                .build();
    }

    @Bean
    public TechnicalExpertWithMemory technicalExpertWithMemory(ChatMemoryProvider chatMemoryProvider, @Qualifier("aliQwenModel") ChatModel chatModel) {
        return AgenticServices
                .agentBuilder(TechnicalExpertWithMemory.class)
                .beforeAgentInvocation(this::log)
                .afterAgentInvocation(this::log)
                .chatModel(chatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .summarizedContext("medical", "legal")
                .outputKey("response")
                .build();
    }

    @Bean
    public MedicalExpertWithMemory medicalExpertWithMemory(ChatMemoryProvider chatMemoryProvider, @Qualifier("aliQwenModel") ChatModel chatModel) {
        return AgenticServices
                .agentBuilder(MedicalExpertWithMemory.class)
                .afterAgentInvocation(this::log)
                .chatModel(chatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .summarizedContext("technical", "legal")
                .outputKey("response")
                .build();
    }


    @Bean("expertRouterAgentWithMemory")
    public ExpertRouterAgentWithMemory expertRouterAgentWithMemory(CategoryRouterWithMemory categoryRouter, MedicalExpertWithMemory medicalExpert,
                                                                   LegalExpertWithMemory legalExpert, TechnicalExpertWithMemory technicalExpert,
                                                                   RedisAgenticScopeStore agenticScopeStore) {
//        AgenticScopePersister.setStore(agenticScopeStore);

        // 构建一个条件工作流 ： 根据分类路由到不同的专家
        UntypedAgent expertsAgent = AgenticServices.conditionalBuilder()
                .subAgents(agenticScope -> agenticScope.readState("category",
                        CategoryRouter.RequestCategory.UNKNOWN) == CategoryRouter.RequestCategory.MEDICAL, medicalExpert)
                .subAgents(agenticScope -> agenticScope.readState("category",
                        CategoryRouter.RequestCategory.UNKNOWN) == CategoryRouter.RequestCategory.LEGAL, legalExpert)
                .subAgents(agenticScope -> agenticScope.readState("category",
                        CategoryRouter.RequestCategory.UNKNOWN) == CategoryRouter.RequestCategory.TECHNICAL, technicalExpert)
                .subAgents(agenticScope -> agenticScope.readState("category",
                        CategoryRouter.RequestCategory.UNKNOWN) == CategoryRouter.RequestCategory.UNKNOWN, technicalExpert)
                .build();

        // 清除某个 MemoryId 记录
        // expertsAgent.evictAgenticScope("");

        // 构建一个顺序工作流 : 先通过 categoryRouter 对问题进行分类分类，再路由到不同的专家 Agent 进行处理
        return AgenticServices
                .sequenceBuilder(ExpertRouterAgentWithMemory.class)
                .beforeAgentInvocation(this::log)
                .afterAgentInvocation(this::log)
                .subAgents(categoryRouter, expertsAgent)
                .outputKey("response")
                .build();
    }


    /***************************************************/


    @Bean("bankAgent")
    public SupervisorAgent bankAgent(ChatModel chatModel) {
        BankTool bankTool = new BankTool();
        // 初始化两个账户：张三和李四，初始余额均为 1000.0
        bankTool.createAccount("张三", 1000.0);
        bankTool.createAccount("李四", 1000.0);

        WithdrawAgent withdrawAgent = AgenticServices
                .agentBuilder(WithdrawAgent.class)
                .chatModel(chatModel)
                .tools(bankTool)
                .build();

        CreditAgent creditAgent = AgenticServices
                .agentBuilder(CreditAgent.class)
                .chatModel(chatModel)
                .tools(bankTool)
                .build();

        BalanceAgent balanceAgent = AgenticServices
                .agentBuilder(BalanceAgent.class)
                .chatModel(chatModel)
                .tools(bankTool)
                .build();

        ExchangeAgent exchangeAgent = AgenticServices
                .agentBuilder(ExchangeAgent.class)
                .chatModel(chatModel)
                .tools(new ExchangeTool())
                .build();

        return AgenticServices
                .supervisorBuilder()
                .chatModel(chatModel)
                .subAgents(withdrawAgent, creditAgent, balanceAgent, exchangeAgent)
//                .subAgents(withdrawAgent, creditAgent, balanceAgent, new ExchangeOperator())
                .responseStrategy(SupervisorResponseStrategy.SUMMARY)
                .build();
    }


    /***************************************************/

    @Bean("horoscopeAgent")
    public SupervisorAgent horoscopeAgent(ChatModel chatModel) {
        AstrologyAgent astrologyAgent = AgenticServices
                .agentBuilder(AstrologyAgent.class)
                .chatModel(chatModel)
                .build();

        HumanInTheLoop humanInTheLoop = AgenticServices
                .humanInTheLoopBuilder()
                .description("一个询问用户星座的智能体")
                .outputKey("sign")
                .requestWriter(request -> {
                    System.out.println(request);
                    System.out.print("> ");
                })
                .responseReader(() -> {
                    Scanner scanner = new Scanner(System.in);
                    return scanner.nextLine();
                })
                .build();

        return AgenticServices
                .supervisorBuilder()
                .chatModel(chatModel)
                .subAgents(astrologyAgent, humanInTheLoop)
                .build();
    }


}
