package com.kingfish.langchain4j.config;

import com.kingfish.langchain4j.service.ai.SkillsAssistant;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.skills.FileSystemSkillLoader;
import dev.langchain4j.skills.Skills;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.nio.file.Path;

/**
 * Remote Server 端 LLM 配置类
 * 注册 StreamingChatModel 和流式 MySQL 对话助手
 *
 * @author haowl
 * @date 2026/4/2 14:00
 */
@Configuration
public class SkillsLLMConfig {
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


    @Bean
    public SkillsAssistant skillsAssistant(ChatModel chatModel) {
        Skills skills = Skills.from(FileSystemSkillLoader.loadSkills(Path.of("skills/")));

        SkillsAssistant service = AiServices.builder(SkillsAssistant.class)
                .chatModel(chatModel)
                .toolProvider(skills.toolProvider()) // or .toolProviders(myToolProvider, skills.toolProvider()) if you already have a tool provider configured
                .systemMessage("You have access to the following skills:\n" + skills.formatAvailableSkills()
                        + "\nWhen the user's request relates to one of these skills, activate it first using the `activate_skill` tool before proceeding.")
                .build();
    }

}
