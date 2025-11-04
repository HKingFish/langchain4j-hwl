package com.kingfish.langchain4j.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingfish.langchain4j.service.ai.PersonExtractor;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;


/**
 * @Author : haowl
 * @Date : 2025/10/29 9:11
 * @Desc :
 */
@Service
public class StructuredLlmServiceImpl implements StructuredLlmService {

    @Resource
    private OpenAiChatModel chatModel;

    @Resource
    private PersonExtractor personExtractor;

    @SneakyThrows
    @Override
    public String chatByChatModel(String userMessage) {
        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(ResponseFormatType.JSON) // type can be either TEXT (default) or JSON
                .jsonSchema(JsonSchema.builder()
                        .name("Person") // OpenAI requires specifying the name for the schema
                        .rootElement(JsonObjectSchema.builder() // see [1] below
                                .addStringProperty("name")
                                .addIntegerProperty("age")
                                .addNumberProperty("height")
                                .addBooleanProperty("married")
                                .required("name", "age", "height", "married") // see [2] below
                                .build())
                        .build())
                .build();
        ChatRequest chatRequest = ChatRequest.builder()
                .responseFormat(responseFormat)
                .messages(UserMessage.from(userMessage))
                .build();


        ChatResponse chatResponse = chatModel.chat(chatRequest);

        String output = chatResponse.aiMessage().text();
        System.out.println(output);

        Person person = new ObjectMapper().readValue(output, Person.class);
        System.out.println(person);

        return person.toString();
    }

    @Override
    public String chatByAiService(String userMessage) {
        return personExtractor.extractPersonFrom(userMessage).toString();
    }


    public record Person(String name, int age, double height, boolean married) {
    }
}
