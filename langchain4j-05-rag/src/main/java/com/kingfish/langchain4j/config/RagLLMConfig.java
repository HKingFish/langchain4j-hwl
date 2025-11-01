package com.kingfish.langchain4j.config;

import com.kingfish.langchain4j.service.ai.EasyRagAssistant;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * @Author : haowl
 * @Date : 2025/10/29 9:40
 * @Desc :
 */
@Slf4j
@Configuration
public class RagLLMConfig {


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


    @Bean("chatMemoryProvider")
    public ChatMemoryProvider chatMemoryProvider(RedisChatMemoryStore chatMemoryStore) {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(20)
                .chatMemoryStore(chatMemoryStore)
                .build();
    }


    /******************************************/


    @Bean
    public EasyRagAssistant easyRagAssistant(ChatModel chatModel, ChatMemoryProvider chatMemoryProvider) {
        // 加载 resources/knowledge目录下的所有文档 （txt文件）
        // 单星号 *：仅匹配当前目录下符合后缀的文件，无法穿透子目录。例如 glob:*.pdf 只能找到 “目标目录根级” 的 PDF，子目录里的 PDF 会被忽略。
        //双星号 **：是 glob 中的 “递归通配符”，可匹配当前目录及所有嵌套子目录。例如 glob:**.pdf 能遍历目标目录下的所有层级（包括子目录、子子目录等），找到所有 PDF 文件，与 loadDocumentsRecursively 的 “递归加载” 逻辑完全适配。

        // glob 语法：匹配所有子目录下的 .txt 和 .md 文件
//        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.{txt,md}");
//        List<Document> documents = ClassPathDocumentLoader.loadDocuments("static/knowledge/", pathMatcher);

        List<Document> documents = ClassPathDocumentLoader.loadDocuments("static/knowledge");
        // 构建一个内存中的向量数据库（EmbeddingStore），用于存储文档的向量表示。
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        return AiServices.builder(EasyRagAssistant.class)
                .chatModel(chatModel)
                .chatMemoryProvider(chatMemoryProvider)
                // 核心作用是从预处理好的数据源（如向量数据库 / 嵌入存储）中，根据用户查询提取出最相关的内容片段，为后续 LLM 生成准确回答提供 “事实依据”，避免模型依赖过时或错误的内置知识。
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();
    }
}