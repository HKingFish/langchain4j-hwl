package com.kingfish.langchain4j.controller;

import com.kingfish.langchain4j.service.ai.SkillsAssistant;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MySQL MCP 远程服务控制器
 * 通过 Streamable HTTP 方式暴露 MCP 协议端点，支持 initialize、tools/list、tools/call 等全部 MCP 方法
 * 同时提供流式对话接口，支持 SSE 逐 token 推送
 *
 * @author haowl
 * @date 2026/3/29 18:16
 */
@RestController
@RequestMapping("/skill")
public class SkillsController {

    @Resource
    private SkillsAssistant skillsAssistant;


    @RequestMapping("chat")
    public String chat(String message) {
        return skillsAssistant.chat(message);
    }

}
