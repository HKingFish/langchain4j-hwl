package com.kingfish.langchain4j.config;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.scoring.ScoringModel;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : haowl
 * @date : 2026/3/9
 * @desc : 阿里云 DashScope Rerank API 的 ScoringModel 实现
 * 调用 gte-rerank-v2 模型对检索结果进行精排，支持中英文等50+语言
 * <p>
 * API 文档：https://www.alibabacloud.com/help/en/model-studio/text-rerank-api
 * <p>
 * 请求格式（gte-rerank-v2）：
 * POST https://dashscope.aliyuncs.com/api/v1/services/rerank/text-rerank/text-rerank
 * {
 * "model": "gte-rerank-v2",
 * "input": {
 * "query": "用户查询",
 * "documents": ["文档1", "文档2", ...]
 * },
 * "parameters": { "return_documents": false }
 * }
 * <p>
 * 响应格式：
 * {
 * "output": {
 * "results": [
 * { "index": 0, "relevance_score": 0.93 },
 * { "index": 1, "relevance_score": 0.12 }
 * ]
 * }
 * }
 * 注意：results 按 relevance_score 降序排列，index 为原始文档在入参列表中的位置
 */
@Slf4j
@Builder
public class DashScopeScoringModel implements ScoringModel {

    /**
     * 阿里云 DashScope Rerank API 地址
     */
    private static final String RERANK_URL =
            "https://dashscope.aliyuncs.com/api/v1/services/rerank/text-rerank/text-rerank";

    /**
     * 使用的重排序模型名称
     * - gte-rerank-v2：支持中英文等50+语言，每次最多500篇文档，单文档最大4000 tokens
     * - qwen3-rerank：支持100+语言，免费额度100万tokens（激活后90天内有效）
     */
    @Builder.Default
    private final String modelName = "gte-rerank-v2";

    /**
     * DashScope API Key，从环境变量 aliQwen-api 读取
     */
    private final String apiKey;

    /**
     * 批量对多个文本与查询的相关性打分（核心方法，实现 ScoringModel 唯一抽象方法）
     * 一次 API 调用返回所有文档的分数，按原始顺序排列
     *
     * @param texts 待评分的文档列表（TextSegment）
     * @param query 用户查询
     * @return 与入参 texts 顺序一一对应的相关性分数列表
     */
    @Override
    public Response<List<Double>> scoreAll(List<TextSegment> texts, String query) {
        // 将 TextSegment 转为字符串列表
        List<String> documents = texts.stream()
                .map(TextSegment::text)
                .toList();
        return scoreAllByStrings(documents, query);
    }

    /**
     * 内部批量打分实现，调用 DashScope Rerank HTTP API
     *
     * @param documents 文档文本列表
     * @param query     用户查询
     * @return 与入参 documents 顺序一一对应的相关性分数列表
     */
    private Response<List<Double>> scoreAllByStrings(List<String> documents, String query) {
        // 构建请求体
        JSONObject requestBody = JSONUtil.createObj()
                .set("model", modelName)
                .set("input", JSONUtil.createObj()
                        .set("query", query)
                        .set("documents", documents))
                .set("parameters", JSONUtil.createObj()
                        // 不返回原始文档内容，减少网络传输
                        .set("return_documents", false));

        log.info("调用 DashScope Rerank API：model={}, query={}, documents数量={}", modelName, query, documents.size());

        try (HttpResponse response = HttpRequest.post(RERANK_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .execute()) {

            if (!response.isOk()) {
                log.error("DashScope Rerank API 调用失败：status={}, body={}", response.getStatus(), response.body());
                throw new RuntimeException("DashScope rerank API failed with status: " + response.getStatus());
            }

            JSONObject responseJson = JSONUtil.parseObj(response.body());
            JSONArray results = responseJson.getByPath("output.results", JSONArray.class);

            // API 返回的 results 按 relevance_score 降序排列，需要按原始 index 还原顺序
            Double[] scores = new Double[documents.size()];
            for (int i = 0; i < results.size(); i++) {
                JSONObject result = results.getJSONObject(i);
                int originalIndex = result.getInt("index");
                double relevanceScore = result.getDouble("relevance_score");
                scores[originalIndex] = relevanceScore;
            }

            // 将结果转为列表，未出现在结果中的文档分数置为 0.0
            List<Double> scoreList = new ArrayList<>(documents.size());
            for (Double score : scores) {
                scoreList.add(score != null ? score : 0.0);
            }

            log.info("DashScope Rerank API 返回分数：{}", scoreList);
            return Response.from(scoreList);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("DashScope rerank API request failed: " + e.getMessage(), e);
        }
    }
}
