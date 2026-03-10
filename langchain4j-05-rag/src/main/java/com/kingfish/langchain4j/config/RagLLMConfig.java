package com.kingfish.langchain4j.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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


//    @Bean
//    public EasyRagAssistant easyRagAssistant(ChatModel chatModel, ChatMemoryProvider chatMemoryProvider) {
//        // 加载 resources/knowledge目录下的所有文档 （txt文件）
//        // 单星号 *：仅匹配当前目录下符合后缀的文件，无法穿透子目录。例如 glob:*.pdf 只能找到 “目标目录根级” 的 PDF，子目录里的 PDF 会被忽略。
//        //双星号 **：是 glob 中的 “递归通配符”，可匹配当前目录及所有嵌套子目录。例如 glob:**.pdf 能遍历目标目录下的所有层级（包括子目录、子子目录等），找到所有 PDF 文件，与 loadDocumentsRecursively 的 “递归加载” 逻辑完全适配。
//
//        // glob 语法：匹配所有子目录下的 .txt 和 .md 文件
////        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.{txt,md}");
////        List<Document> documents = ClassPathDocumentLoader.loadDocuments("static/knowledge/", pathMatcher);
//
//        List<Document> documents = ClassPathDocumentLoader.loadDocuments("static/knowledge");
//        // 构建一个内存中的向量数据库（EmbeddingStore），用于存储文档的向量表示。
//        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
//        EmbeddingStoreIngestor.ingest(documents, embeddingStore);
//
//        return AiServices.builder(EasyRagAssistant.class)
//                .chatModel(chatModel)
//                .chatMemoryProvider(chatMemoryProvider)
//                // 核心作用是从预处理好的数据源（如向量数据库 / 嵌入存储）中，根据用户查询提取出最相关的内容片段，为后续 LLM 生成准确回答提供 “事实依据”，避免模型依赖过时或错误的内置知识。
//                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
//                .build();
//    }


//    @Bean
//    public AdvancedRagAssistant advancedRagAssistant(ChatModel chatModel, ChatMemoryProvider chatMemoryProvider) {
//        List<Document> documents = ClassPathDocumentLoader.loadDocuments("static/knowledge");
//        // 构建一个内存中的向量数据库（EmbeddingStore），用于存储文档的向量表示。
//        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
//        EmbeddingStoreIngestor.ingest(documents, embeddingStore);
//
//        // 用户查询
//        // -> Query(原始查询内容)
//        // -> QueryTransformer : 转为更合规的 Query)
//        // -> QueryRouter : 根据 Query 的内容路由到匹配的 ContentRetriever)
//        // -> ContentRetriever : 进行内容检索，输出 Content)
//        // -> contentAggregator : 对输出的 Content 进行排序聚合)
//        // -> contentInjector : 将最终的 Content 注入到 UserMessage 中一起发给 LLM
//        DefaultRetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
//                // 指定 查询转换器 ：将查询内容转化为更合规的 Query 内容
//                .queryTransformer()
//                // 指定 查询路由器 ：将用户查询的 Query 分配到对应的 contentRetriever上
//                .queryRouter()
//                // 指定 内容检索器 ：筛选与 Query 相关的 Content 内容
//                .contentRetriever()
//                // 指定 内容整合器 ：整合来自不同渠道的已排序 Content 列表
//                .contentAggregator()
//                // 指定 内容注入器 ：将筛选整合后的 Content 注入到 UserMessage 中
//                .contentInjector()
//                // 指定 查询(Query)和检索(ContentRetriever)时的线程处理机制
//                .executor()
//                .build();
//
//        return AiServices.builder(AdvancedRagAssistant.class)
//                .chatModel(chatModel)
//                .chatMemoryProvider(chatMemoryProvider)
//                .retrievalAugmentor(retrievalAugmentor)
//                .build();
//    }

//    /**
//     * 联网搜索 :需先引入对应 `WebSearchEngine` 集成的依赖（如谷歌搜索集成、必应搜索集成等），并配置搜索引擎的 API 密钥
//     * @return
//     */
//    @Bean
//    public ContentRetriever contentRetriever(){
//        WebSearchEngine googleSearchEngine = GoogleCustomWebSearchEngine.builder()
//                .apiKey(System.getenv("GOOGLE_API_KEY"))
//                .csi(System.getenv("GOOGLE_SEARCH_ENGINE_ID"))
//                .build();
//
//        return WebSearchContentRetriever.builder()
//                .webSearchEngine(googleSearchEngine)
//                .maxResults(3)
//                .build();
//    }

}