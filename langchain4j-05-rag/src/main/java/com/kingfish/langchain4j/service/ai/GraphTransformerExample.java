//package com.kingfish.langchain4j.service.ai;
//
//import dev.langchain4j.community.data.document.transformer.graph.LLMGraphTransformer;
//import dev.langchain4j.data.document.Document;
//import dev.langchain4j.model.openai.OpenAiChatModel;
//import dev.langchain4j.community.data.document.graph.GraphDocument;
//import dev.langchain4j.community.data.document.graph.GraphNode;
//import dev.langchain4j.community.data.document.graph.GraphEdge;
//import dev.langchain4j.community.data.document.transformer.graph.GraphTransformer;
//
//import java.time.Duration;
//import java.util.Set;
//
//public class GraphTransformerExample {
//    public static void main(String[] args) {
//        // Create a GraphTransformer backed by an LLM
//        GraphTransformer transformer = new LLMGraphTransformer(
//            OpenAiChatModel.builder()
//                .apiKey(System.getenv("OPENAI_API_KEY"))
//                .timeout(Duration.ofSeconds(60))
//                .build()
//        );
//
//        // Input document
//        Document document = Document.from("Barack Obama was born in Hawaii and served as the 44th President of the United States.");
//
//        // Transform the document
//        GraphDocument graphDocument = transformer.transform(document);
//
//        // Access nodes and relationships
//        Set<GraphNode> nodes = graphDocument.nodes();
//        Set<GraphEdge> relationships = graphDocument.relationships();
//
//        nodes.forEach(System.out::println);
//        relationships.forEach(System.out::println);
//    }
//}