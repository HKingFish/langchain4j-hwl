package com.kingfish.langchain4j.service;

import com.kingfish.langchain4j.service.agents.BookAssistant;
import com.kingfish.langchain4j.service.agents.CalculatorAssistant;
import com.kingfish.langchain4j.service.agents.DynamicAssistant;
import com.kingfish.langchain4j.service.agents.StreamCalculatorAssistant;
import com.kingfish.langchain4j.service.ai.ExpertRouterAgent;
import com.kingfish.langchain4j.tools.WeatherTools;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import dev.langchain4j.service.tool.ToolExecution;
import dev.langchain4j.service.tool.ToolExecutor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author : haowl
 * @Date : 2025/10/29 9:11
 * @Desc :
 */
@Slf4j
@Service
public class ToolsLlmServiceImpl implements ToolsLlmService {

    @Resource
    private OpenAiChatModel chatModel;

    @Resource(name = "aliQwenModel")
    private ChatModel aliQwenModel;

    @Resource
    private ExpertRouterAgent expertRouterAgent;

    @Resource
    private BookAssistant bookAssistant;

    @Resource
    private DynamicAssistant dynamicAssistant;

    @Resource
    private CalculatorAssistant calculatorAssistant;

    @Resource
    private StreamCalculatorAssistant streamCalculatorAssistant;


    @Override
    public String chatByLowLevelApi(String userMessage) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(UserMessage.from(userMessage));

        // 1. 构建 Tool 信息
        WeatherTools weatherTools = new WeatherTools();
        List<ToolSpecification> toolSpecifications =
                ToolSpecifications.toolSpecificationsFrom(weatherTools);
        ChatRequest request = ChatRequest.builder()
                .messages(chatMessages)
                .toolSpecifications(toolSpecifications)
                .build();
        // 2. 发起第一次 LLM 调用
        ChatResponse response = chatModel.chat(request);
        AiMessage aiMessage = response.aiMessage();
        // 3. 如果需要调用 tool，需要执行 tool 调用
        if (aiMessage.hasToolExecutionRequests()) {
            // 将AI的工具调用消息添加到消息列表（作为工具结果的前置消息）,
            // 否则会提示 Invalid parameter: messages with role 'tool' must be a response to a preceeding message with 'tool_calls'.",
            chatMessages.add(aiMessage);
            // 4. 执行工具调用
            for (ToolExecutionRequest toolExecutionRequest : aiMessage.toolExecutionRequests()) {
                ToolExecutor toolExecutor = new DefaultToolExecutor(weatherTools, toolExecutionRequest);
                String result = toolExecutor.execute(toolExecutionRequest, "1");
                ToolExecutionResultMessage toolExecutionResultMessages =
                        ToolExecutionResultMessage.from(toolExecutionRequest, result);
                chatMessages.add(toolExecutionResultMessages);
            }

            // 5. 将工具执行结果作为新的消息，发起 LLM 调用
            ChatRequest chatRequest2 = ChatRequest.builder()
                    .messages(chatMessages)
                    .parameters(ChatRequestParameters.builder()
                            .toolSpecifications(toolSpecifications)
                            .build())
                    .build();
            ChatResponse finalChatResponse = chatModel.chat(chatRequest2);
            return finalChatResponse.aiMessage().text();
        } else {
            // 不需要调用工具直接返回
            return aiMessage.text();
        }
    }

    @Override
    public String chatByHighLevelApi(String userMessage) {
        return "";
    }

    @Override
    public String chatWithExpert(String userMessage) {
        return expertRouterAgent.ask(userMessage);
    }

    @Override
    public String bookDetail(String bookName) {
        return bookAssistant.chat(bookName);
    }

    @Override
    public String dynamicChat(String userMessage) {
        return dynamicAssistant.chat(userMessage);
    }

    @Override
    public String calculator(String userMessage) {
        Result<String> result = calculatorAssistant.chat(userMessage);
        // 从 result 中获取工具执行的信息
        List<ToolExecution> toolExecutions = result.toolExecutions();

        ToolExecution toolExecution = toolExecutions.get(0);
        ToolExecutionRequest request = toolExecution.request();
        // 获取工具执行结果的“原始对象”
        Object resultObject = toolExecution.resultObject();
        // 结果需要从 toolExecutions 中获取 ：获取工具执行结果的“文本形式”
        return toolExecution.result();
    }

    @Override
    public Flux<String> calculatorStream(String userMessage) {
        return Flux.create(emitter ->
                streamCalculatorAssistant.chat(userMessage)
                        // 打印 Tool 信息 :当某个工具执行完成时，会自动触发此回调，参数 `toolExecution` 包含该工具的调用详情（同普通模式的 `ToolExecution`，可获取请求、结果等
                        // 因为
                        .onToolExecuted(toolExecution -> emitter.next(toolExecution.result()))
                        .onPartialResponse(emitter::next)
                        .onCompleteResponse(completeResponse -> emitter.complete())
                        .onError(emitter::error)
                        .start());
    }
}
