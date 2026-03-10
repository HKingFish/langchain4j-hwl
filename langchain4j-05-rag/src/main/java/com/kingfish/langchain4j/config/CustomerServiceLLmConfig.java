package com.kingfish.langchain4j.config;

import com.kingfish.langchain4j.service.ai.CustomerServiceAssistant;
import com.kingfish.langchain4j.service.ai.CustomerServiceRouter;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallzhv15q.BgeSmallZhV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.ReRankingContentAggregator;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.IngestionResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;

/**
 * @author : haowl
 * @date : 2026/3/9 19:51
 * @desc : 智能客服构建
 */
@Slf4j
@Configuration
public class CustomerServiceLLmConfig {
    public static final String API_KEY = "Your Api Key";

    /**
     * 初始化嵌入模型（阿里云 text-embedding-v3，支持中文）
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .apiKey(API_KEY)
                .modelName("text-embedding-v3")
                .build();
    }

    /**
     * 意图分类路由器：识别用户意图（产品咨询/订单查询/投诉建议/通用聊天）
     */
    @Bean
    public CustomerServiceRouter customerServiceRouter(ChatModel chatModel) {
        return AiServices.builder(CustomerServiceRouter.class)
                .chatModel(chatModel)
                .build();
    }

    /**
     * 智能客服 Assistant（统一入口）
     * <p>
     * RAG 流水线：
     * 用户查询
     * → QueryTransformer  ：短查询补充领域关键词，提升检索召回率
     * → QueryRouter       ：根据用户意图路由到对应的 ContentRetriever
     * - PRODUCT_INQUIRY → 产品知识库检索器（向量相似度检索）
     * - 其他意图        → 空检索器（由 SystemMessage 提示词驱动）
     * → ContentRetriever  ：向量检索，召回候选文档片段
     * → ContentAggregator ：LLM 打分重排序，精选 top3 注入上下文
     * → ContentInjector   ：将检索内容以结构化格式注入 UserMessage
     * → LLM
     */
    @Bean
    public CustomerServiceAssistant customerServiceAssistant(
            ChatModel chatModel,
            ChatMemoryProvider chatMemoryProvider,
            CustomerServiceRouter customerServiceRouter) {

        ContentRetriever productRetriever = buildProductRetriever();
        ContentRetriever emptyRetriever = query -> List.of();

        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                // 1. QueryTransformer：短查询补充"产品"关键词，提升向量检索召回率
                //    CompressingQueryTransformer 可结合对话历史压缩改写，适合多轮对话场景
                .queryTransformer(this::buildQueryTransformer)
                // 2. QueryRouter：根据用户意图路由到对应的 ContentRetriever
                //    - PRODUCT_INQUIRY → 产品知识库检索器
                //    - 其他意图        → 空检索器（由 SystemMessage 提示词驱动回答）
                .queryRouter(query -> buildQueryRouter(query, customerServiceRouter, productRetriever, emptyRetriever))
                // 3. ContentAggregator：LLM 打分重排序
                //    功能验证方案：复用 ChatModel 对每条候选文档打 0~10 分，精选 top3
                //    生产环境建议替换为 DashScopeScoringModel（调用 gte-rerank-v2 API，延迟更低）
                .contentAggregator(buildReRankingAggregator())
                // 4. ContentInjector：将检索内容以带序号格式注入 UserMessage
                .contentInjector(this::buildContentInjector)
                // 5. Executor：多路检索时可指定线程池并行执行，降低延迟
                // .executor(Executors.newFixedThreadPool(4))
                .build();

        return AiServices.builder(CustomerServiceAssistant.class)
                .chatModel(chatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }

    /**
     * 构建产品知识库检索器
     * 加载 static/knowledge2 目录文档，拆成500令牌/段，重叠100令牌
     * 检索时最多召回5条候选，相似度阈值0.6
     */
    private ContentRetriever buildProductRetriever() {
        log.info("智能客服产品知识库加载开始");
        EmbeddingModel embeddingModel = new BgeSmallZhV15QuantizedEmbeddingModel();
        List<Document> documents = ClassPathDocumentLoader.loadDocuments("static/knowledge2");
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        IngestionResult result = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(500, 100, new OpenAiTokenCountEstimator("gpt-4o-mini")))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build()
                .ingest(documents);
        log.info("智能客服产品知识库加载完成，tokenUsage: {}", result.tokenUsage());
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .minScore(0.6)
                .build();
    }

    /**
     * QueryTransformer：对原始查询进行改写
     * 若查询过短（少于5个字），补充"产品"关键词辅助向量检索
     */
    private List<Query> buildQueryTransformer(Query query) {
        if (query.text().length() < 5) {
            return List.of(Query.from("产品: " + query.text(), query.metadata()));
        }
        return List.of(query);
    }

    /**
     * QueryRouter：根据用户意图决定走哪个 ContentRetriever
     * PRODUCT_INQUIRY → 产品知识库检索器；其他意图 → 空检索器
     */
    private Collection<ContentRetriever> buildQueryRouter(
            Query query,
            CustomerServiceRouter customerServiceRouter,
            ContentRetriever productRetriever,
            ContentRetriever emptyRetriever) {
        CustomerServiceRouter.UserIntent intent = customerServiceRouter.classify(query.text());
        log.info("智能客服意图识别：intent={}, query={}", intent, query.text());
        if (intent == CustomerServiceRouter.UserIntent.PRODUCT_INQUIRY) {
            return List.of(productRetriever);
        }
        return List.of(emptyRetriever);
    }

    /**
     * ContentInjector：将检索内容以带序号的格式注入 UserMessage
     */
    private dev.langchain4j.data.message.ChatMessage buildContentInjector(
            List<dev.langchain4j.rag.content.Content> contents,
            dev.langchain4j.data.message.ChatMessage userMessage) {
        if (contents.isEmpty()) {
            return userMessage;
        }
        StringBuilder sb = new StringBuilder(((UserMessage) userMessage).singleText());
        sb.append("\n\n请参考以下知识库内容作答：\n");
        for (int i = 0; i < contents.size(); i++) {
            sb.append(i + 1).append(". ").append(contents.get(i).textSegment().text()).append("\n");
        }
        log.info("userMessage={}", sb.toString());
        return UserMessage.from(sb.toString());
    }


    /**
     * 构建重排序聚合器
     * <p>
     * 使用阿里云 DashScope gte-rerank-v2 模型对向量检索结果进行精排：
     * 1. 向量检索（EmbeddingStoreContentRetriever）先召回 maxResults=5 条候选文档
     * 2. ReRankingContentAggregator 调用 Rerank API 对5条候选文档重新打分排序
     * 3. 最终只保留分数最高的 3 条注入 UserMessage
     * <p>
     * 相比纯向量检索的优势：
     * - 向量检索基于语义相似度（Bi-Encoder），速度快但精度有限
     * - Rerank 模型（Cross-Encoder）对 query 和文档联合编码，精度更高
     * - 两阶段组合：召回阶段用向量检索保证速度，精排阶段用 Rerank 保证质量
     * <p>
     * DashScope gte-rerank-v2 模型说明：
     * - 支持中英文等50+语言，适合本项目中文知识库场景
     * - 单次最多500篇文档，单文档最大4000 tokens
     * - 计费：$0.115 / 1M tokens
     * - API 文档：https://www.alibabacloud.com/help/en/model-studio/text-rerank-api
     */
    private ContentAggregator buildReRankingAggregator() {
        DashScopeScoringModel scoringModel = DashScopeScoringModel.builder()
                .apiKey(API_KEY)
                // gte-rerank-v2：支持中英文等50+语言
                // 可替换为 qwen3-rerank：支持100+语言，有免费额度
                .modelName("gte-rerank-v2")
                .build();

        return ReRankingContentAggregator.builder()
                .scoringModel(scoringModel)
                // 从向量检索的5条候选中精选3条注入 LLM 上下文
                // 减少 token 消耗的同时保证内容质量
                .maxResults(3)
                .build();
    }
}
