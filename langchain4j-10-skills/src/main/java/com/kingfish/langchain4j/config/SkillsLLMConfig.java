package com.kingfish.langchain4j.config;

import com.kingfish.langchain4j.service.ai.SkillsAssistant;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.observability.api.listener.ToolExecutedEventListener;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.skills.FileSystemSkillLoader;
import dev.langchain4j.skills.Skills;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.nio.file.Path;

/**
 * Skills + MCP 联合配置类
 * 将 MCP 工具和 Skills 同时注册到 AI Service，
 * LLM 激活 Skill 后按照指令选择调用不同的 MCP 工具
 *
 * @author haowl
 * @date 2026/4/2 14:00
 */
@Slf4j
@Configuration
public class SkillsLLMConfig {

    /**
     * MCP 远程服务地址（MySQL MCP Server）
     */
    private static final String MCP_REMOTE_SERVER_URL =
            "http://localhost:8080/langchain4j/mcp/remote-mysql/mcpTools";

    /**
     * MCP 传输层超时时间（秒）
     */
    private static final int MCP_TRANSPORT_TIMEOUT_SECONDS = 60;

    /**
     * Skill 文件所在目录
     */
    private static final String SKILLS_DIRECTORY = "C:\\SelfWork\\code\\github\\langchain4j-hwl\\langchain4j-10-skills\\src\\main\\resources\\skills";

    /**
     * OpenAI 模型
     *
     * @return OpenAiChatModel 实例
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


    /**
     * 构建 Skills + MCP 联合助手
     * 同时挂载 Skills 的 toolProvider（提供 activate_skill、read_skill_resource）
     * 和 MCP 的 toolProvider（提供 listTables、describeTable、executeQuery、executeUpdate）
     * LLM 激活 Skill 后，按照 SKILL.md 中的策略指令选择调用对应的 MCP 工具
     *
     * @param chatModel 聊天模型
     * @return SkillsAssistant 实例
     */
    @Bean
    public SkillsAssistant skillsAssistant(ChatModel chatModel) {
        // 1. 加载 MCP 服务
        McpTransport transport = StreamableHttpMcpTransport.builder()
                .url(MCP_REMOTE_SERVER_URL)
                .build();

        // MCP 工具提供者（提供数据库操作相关工具）
        ToolProvider mcpToolProvider = McpToolProvider.builder()
                .mcpClients(DefaultMcpClient.builder()
                        .key("MysqlSkillsMcp")
                        .transport(transport)
                        .build())
                .build();


        // 从文件系统加载 Skills（包含 database-ops 等技能）
        Skills skills = Skills.from(FileSystemSkillLoader.loadSkills(Path.of(SKILLS_DIRECTORY)));


        // 工具调用监听器，记录每次工具调用的名称和来源，用于区分 Skills 触发还是直接触发
        // 因为即使不通过 Skill 该逻辑也是能跑通，这里为了验证确实激活了 Skills 打印了下面的日志。
        ToolExecutedEventListener toolExecutedListener = event -> {
            String toolName = event.request().name();
            String arguments = event.request().arguments();

            if ("activate_skill".equals(toolName)) {
                log.info("【Skills 触发】激活技能，参数：{}", arguments);
            } else if ("read_skill_resource".equals(toolName)) {
                log.info("【Skills 触发】读取技能资源，参数：{}", arguments);
            } else {
                log.info("【MCP 工具调用】工具名：{}，参数：{}", toolName, arguments);
            }

            // 打印工具执行结果
            String resultText = event.resultText();
            log.info("【工具执行结果】{}", resultText);
        };

        return AiServices.builder(SkillsAssistant.class)
                .chatModel(chatModel)
                // 使用 CompositeToolProvider 组合 Skills 工具和 MCP 工具
                .toolProvider(CompositeToolProvider.of(skills.toolProvider(), mcpToolProvider))
                // 注册工具调用监听器，记录调用链路
                .registerListener(toolExecutedListener)
                .systemMessage("你是一个智能数据库运维助手。\n"
                        + "你可以使用以下技能：\n"
                        + skills.formatAvailableSkills() + "\n"
                        + "当用户的请求涉及某个技能时，请先使用 `activate_skill` 工具激活对应技能，"
                        + "然后按照技能指令中的策略选择合适的 MCP 工具完成任务。")
                .build();
    }
}
