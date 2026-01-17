package com.kingfish.langchain4j.config;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agentic.scope.AgenticScopeKey;
import dev.langchain4j.agentic.scope.AgenticScopeStore;
import dev.langchain4j.agentic.scope.DefaultAgenticScope;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author : haowl
 * @Date : 2025/11/9 13:32
 * @Desc :
 */
@Component
public class RedisAgenticScopeStore implements AgenticScopeStore {

    public static final String AGENTIC_SCOPE_HASH_KEY = "AgenticScope";

    @Resource
    private RedisTemplate<String, DefaultAgenticScope> redisTemplate;
    // 2. Jackson 用于 JSON 序列化/反序列化
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()); // 支持LocalDateTime序列化


    @SneakyThrows
    @Override
    public boolean save(AgenticScopeKey key, DefaultAgenticScope agenticScope) {
        redisTemplate.opsForHash()
                .put(AGENTIC_SCOPE_HASH_KEY, key.agentId() + ":" + key.memoryId(), objectMapper.writeValueAsString(agenticScope));
        return true;
    }

    @Override
    public Optional<DefaultAgenticScope> load(AgenticScopeKey key) {
        Object object = redisTemplate.opsForHash().get(AGENTIC_SCOPE_HASH_KEY, key.agentId() + ":" + key.memoryId());
        if (object == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(JSONUtil.toBean(
                object.toString(), DefaultAgenticScope.class));
    }

    @Override
    public boolean delete(AgenticScopeKey key) {
        return redisTemplate.delete(key.agentId() + ":" + key.memoryId());
    }

    @Override
    public Set<AgenticScopeKey> getAllKeys() {
        // TODO : 不要使用 keys ，这里纯测试使用
        return redisTemplate.opsForHash().keys(AGENTIC_SCOPE_HASH_KEY)
                .stream()
                .map(key -> {
                    String[] parts = key.toString().split(":");
                    return new AgenticScopeKey(parts[0], parts[1]);
                })
                .collect(Collectors.toSet());
    }
}