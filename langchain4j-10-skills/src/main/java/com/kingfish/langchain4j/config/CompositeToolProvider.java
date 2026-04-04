package com.kingfish.langchain4j.config;

import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 组合工具提供者
 * 将多个 ToolProvider 的工具合并到一个 ToolProviderResult 中返回，
 * 解决低版本 AiServices 不支持 toolProviders(ToolProvider...) 的问题。
 * 同时合并各 ToolProvider 返回的 immediateReturnToolNames
 *
 * @author haowl
 * @date 2026/4/3
 */
@Slf4j
public class CompositeToolProvider implements ToolProvider {

    private final List<ToolProvider> delegates;

    /**
     * 私有构造，通过静态工厂方法创建
     *
     * @param delegates 需要组合的 ToolProvider 列表
     */
    private CompositeToolProvider(List<ToolProvider> delegates) {
        this.delegates = Collections.unmodifiableList(delegates);
    }

    /**
     * 组合多个 ToolProvider 为一个
     *
     * @param providers 需要组合的 ToolProvider（至少传入一个）
     * @return 组合后的 ToolProvider 实例
     * @throws IllegalArgumentException 未传入任何 ToolProvider 时抛出
     */
    public static CompositeToolProvider of(ToolProvider... providers) {
        if (providers == null || providers.length == 0) {
            throw new IllegalArgumentException("至少需要传入一个 ToolProvider");
        }
        return new CompositeToolProvider(Arrays.asList(providers));
    }

    /**
     * 从列表创建组合 ToolProvider
     *
     * @param providers 需要组合的 ToolProvider 列表（至少包含一个）
     * @return 组合后的 ToolProvider 实例
     * @throws IllegalArgumentException 列表为空时抛出
     */
    public static CompositeToolProvider of(List<ToolProvider> providers) {
        if (providers == null || providers.isEmpty()) {
            throw new IllegalArgumentException("至少需要传入一个 ToolProvider");
        }
        return new CompositeToolProvider(new ArrayList<>(providers));
    }

    /**
     * 遍历所有委托的 ToolProvider，收集工具规格、执行器和立即返回工具名，合并为一个结果返回。
     * 单个 ToolProvider 异常不会影响其他 ToolProvider 的工具收集
     *
     * @param request 工具提供请求上下文
     * @return 合并后的 ToolProviderResult，包含所有 ToolProvider 提供的工具
     */
    @Override
    public ToolProviderResult provideTools(ToolProviderRequest request) {
        ToolProviderResult.Builder builder = ToolProviderResult.builder();
        Set<String> mergedImmediateReturnToolNames = new HashSet<>();

        for (ToolProvider delegate : delegates) {
            try {
                ToolProviderResult result = delegate.provideTools(request);
                if (result == null) {
                    continue;
                }

                // 合并工具规格和执行器
                if (result.tools() != null && !result.tools().isEmpty()) {
                    builder.addAll(result.tools());
                    log.debug("从 {} 收集到 {} 个工具",
                            delegate.getClass().getSimpleName(),
                            result.tools().size());
                }

                // 合并立即返回工具名
                // immediateReturnToolNames 的作用是：当 LLM 调用了这个集合中的某个工具后，工具的执行结果会直接返回给用户，而不是再送回 LLM 做进一步处理。
                // 正常的工具调用流程是这样的： 用户提问 → LLM → 调用工具 → 工具返回结果 → 结果送回 LLM → LLM 生成最终回复 → 返回用户
                // 如果工具名在 immediateReturnToolNames 中，流程变成：用户提问 → LLM → 调用工具 → 工具返回结果 → 直接返回用户（跳过 LLM 二次处理）
                if (result.immediateReturnToolNames() != null) {
                    mergedImmediateReturnToolNames.addAll(result.immediateReturnToolNames());
                }
            } catch (Exception e) {
                log.error("从 {} 收集工具时发生异常，已跳过该 ToolProvider，异常信息：{}",
                        delegate.getClass().getSimpleName(), e.getMessage(), e);
            }
        }

        if (!mergedImmediateReturnToolNames.isEmpty()) {
            builder.immediateReturnToolNames(mergedImmediateReturnToolNames);
        }

        return builder.build();
    }
}
