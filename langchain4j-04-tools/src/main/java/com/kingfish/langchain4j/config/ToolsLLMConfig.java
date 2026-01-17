package com.kingfish.langchain4j.config;

import com.kingfish.langchain4j.service.agents.BookAssistant;
import com.kingfish.langchain4j.service.agents.CalculatorAssistant;
import com.kingfish.langchain4j.service.agents.DynamicAssistant;
import com.kingfish.langchain4j.service.agents.StreamCalculatorAssistant;
import com.kingfish.langchain4j.service.ai.ExpertRouterAgent;
import com.kingfish.langchain4j.service.ai.LegalExpert;
import com.kingfish.langchain4j.service.ai.MedicalExpert;
import com.kingfish.langchain4j.service.ai.TechnicalExpert;
import com.kingfish.langchain4j.tools.CalculatorWithImmediateReturn;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderResult;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;

import static dev.langchain4j.internal.Json.fromJson;

/**
 * @Author : haowl
 * @Date : 2025/10/29 9:40
 * @Desc :
 */
@Configuration
public class ToolsLLMConfig {

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

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
                .logRequests(true)
                .logResponses(true)
                .modelName("gpt-4o-mini")
                .build();
    }

    @Bean("openAiStreamingChatModel")
    public OpenAiStreamingChatModel openAiStreamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
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


    @Bean("aliQwenModel")
    public ChatModel ImageModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("aliQwen-api"))
                .modelName("qwen2.5-omni-7b")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .build();
    }


    /***************************/


    @Bean
    public ExpertRouterAgent expertRouterAgent(ChatModel chatModel) {
        MedicalExpert medicalExpert = AiServices.builder(MedicalExpert.class)
                .chatModel(chatModel)
                .build();
        LegalExpert legalExpert = AiServices.builder(LegalExpert.class)
                .chatModel(chatModel)
                .build();
        TechnicalExpert technicalExpert = AiServices.builder(TechnicalExpert.class)
                .chatModel(chatModel)
                .build();

        return AiServices.builder(ExpertRouterAgent.class)
                .chatModel(chatModel)
                .tools(medicalExpert, legalExpert, technicalExpert)
                .build();
    }


    /*******************************************/

    @Bean
    public BookAssistant bookAssistant(ChatModel chatModel) {
        // 自定义工具
        ToolSpecification toolSpecification = ToolSpecification.builder()
                .name("bookDetail")
                .description("返回书籍详情")
                .parameters(JsonObjectSchema.builder()
                        .addProperties(Map.of(
                                "bookName", JsonStringSchema.builder()
                                        .description("书籍名称")
                                        .build()
                        ))
                        .build())
                .build();

        // 自定义工具执行的逻辑
        ToolExecutor toolExecutor = (toolExecutionRequest, memoryId) -> {
            Map<String, Object> arguments = fromJson(toolExecutionRequest.arguments(), Map.class);
            String bookName = arguments.get("bookName").toString();
            // TODO : 自定义数据获取方式
            if ("三国野史".equals(bookName)) {
                return "诸葛亮老婆黄月英是司马懿假扮的";
            }
            return "未找到书籍";
        };

        // 创建 AI Service 服务
        return AiServices.builder(BookAssistant.class)
                .chatModel(chatModel)
                .tools(Map.of(toolSpecification, toolExecutor))
                .build();
    }


    @Bean
    public DynamicAssistant dynamicAssistant(ChatModel chatModel) {
        ToolProvider toolProvider = request -> {
            // 根据用户请求消息自主选择使用哪个工具
            if (request.userMessage().singleText().contains("野史")) {
                // TODO : 做一下缓存
                // 自定义工具
                ToolSpecification toolSpecification = ToolSpecification.builder()
                        .name("bookDetail")
                        .description("返回书籍详情")
                        .parameters(JsonObjectSchema.builder()
                                .addProperties(Map.of(
                                        "bookName", JsonStringSchema.builder()
                                                .description("书籍名称")
                                                .build()
                                ))
                                .build())
                        .build();

                // 自定义工具执行的逻辑
                ToolExecutor toolExecutor = (toolExecutionRequest, memoryId) -> {
                    Map<String, Object> arguments = fromJson(toolExecutionRequest.arguments(), Map.class);
                    String bookName = arguments.get("bookName").toString();
                    // TODO : 自定义数据获取方式
                    if ("三国野史".equals(bookName)) {
                        return "诸葛亮老婆黄月英是司马懿假扮的";
                    }
                    return "未找到书籍";
                };

                return ToolProviderResult.builder()
                        .add(toolSpecification, toolExecutor)
                        .build();
            } else {
                // TODO : 使用其他的工具
            }

            return null;
        };


        // 创建 AI Service 服务
        return AiServices.builder(DynamicAssistant.class)
                .chatModel(chatModel)
                .toolProvider(toolProvider)
                .build();
    }


    /***************************************************/

    @Bean
    public CalculatorAssistant calculatorAssistant(ChatModel chatModel) {
        return AiServices.builder(CalculatorAssistant.class)
                .chatModel(chatModel)
                .tools(new CalculatorWithImmediateReturn())
//                .hallucinatedToolNameStrategy(toolExecutionRequest -> ToolExecutionResultMessage.from(
//                        toolExecutionRequest, "Error: there is no tool called " + toolExecutionRequest.name()))
//                .toolArgumentsErrorHandler((error, context) -> ToolErrorHandlerResult.text("调用参数错误"))
//                .toolExecutionErrorHandler((error, context) -> ToolErrorHandlerResult.text("调用过程错误"))
                .build();
    }

    @Bean
    public StreamCalculatorAssistant streamCalculatorAssistant() {
        return AiServices.builder(StreamCalculatorAssistant.class)
                .streamingChatModel(aliQwenStreamingChatModel())
                .tools(new CalculatorWithImmediateReturn())
                .build();
    }

}
