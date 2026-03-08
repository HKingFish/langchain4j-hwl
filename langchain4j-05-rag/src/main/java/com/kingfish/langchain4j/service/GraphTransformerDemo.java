package com.kingfish.langchain4j.service;

import dev.langchain4j.community.data.document.graph.GraphDocument;
import dev.langchain4j.community.data.document.graph.GraphEdge;
import dev.langchain4j.community.data.document.graph.GraphNode;
import dev.langchain4j.community.data.document.transformer.graph.LLMGraphTransformer;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.List;
import java.util.Set;

/**
 * @Author : haowl
 * @Date : 2026/3/8 19:10
 * @Desc :
 */
public class GraphTransformerDemo {

    public static void main(String[] args) {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();

        String examples = """
                示例1：文本='张三是李四的父亲，李四和王五是同事'
                → 节点：Person(张三)、Person(李四)、Person(王五)
                → 关系：FATHER(张三→李四)、COLLEAGUE(李四→王五)
                """;
        LLMGraphTransformer graphTransformer = LLMGraphTransformer.builder()
                .model(chatModel)
                // 约束输出图谱中仅包含指定类型的节点，防止无关节点混入。
                // - 传 List.of("Person", "Company") → 仅提取 “人物 / 公司” 类型节点；
                // - 传 null/空 → 允许所有节点类型（如文本中出现的 “地点”“事件” 都提取）。
                .allowedNodes(List.of("Person"))
                // 约束输出图谱中仅包含指定类型的关系，过滤无关关系。
                // - 传 List.of("FATHER", "COLLEAGUE") → 仅提取 “父子 / 同事” 关系；
                // - 传 null/空 → 允许所有关系类型（如 “朋友”“上级” 都提取）。
                .allowedRelationships(null)
                // 	对默认提示词补充说明，避免重写整个 prompt。
                // - 传 "提取时忽略所有无关的地点信息" → 仅追加该指令，不改变原有 prompt 核心逻辑。
                .additionalInstructions("用中文描述关系")
                // 提供 “文本→图谱” 的示例，让 LLM 更精准理解提取规则
                // - 传 "示例1：文本='张三是李四的父亲' → 节点：Person(张三)、Person(李四)；关系：FATHER(张三→李四)"
                .examples(examples)
                // LLM 生成结果不符合要求（如格式错误）时，自动重试的次数。
                .maxAttempts(1)
                .build();

        String demoText = """
                马十三是一家律所的合伙人，他的徒弟是律师冯十四。冯十四的丈夫陈十五是某国企的财务总监，陈十五的表姐林十六是马十三的妻子，林十六的弟弟林十七是陈十五的下属（出纳）。林十七的女朋友欧阳十八是冯十四的客户，欧阳十八的父亲欧阳十九曾委托马十三处理过一起经济纠纷，欧阳十九的生意伙伴黄二十是林十六的高中同学，黄二十的儿子黄二一和冯十四的侄子冯十五是同班同学，且黄二一的母亲张二十二是马十三的大学导师。           
                """;
        Document document = Document.document(demoText);
        GraphDocument graphDocument =
                graphTransformer.transform(document);
        // Access nodes and relationships
        Set<GraphNode> nodes = graphDocument.nodes();
        Set<GraphEdge> relationships = graphDocument.relationships();

        nodes.forEach(System.out::println);
        relationships.forEach(System.out::println);
    }
}