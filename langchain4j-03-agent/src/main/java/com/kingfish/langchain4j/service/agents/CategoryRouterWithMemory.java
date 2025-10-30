package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface CategoryRouterWithMemory {

    @UserMessage("""
            分析以下用户请求，并将其归类为“法律的”“医疗的”或“技术的”。如果该请求不属于上述任何类别，则将其归类为“未知的”。仅用其中一个词回复，不要添加其他内容。用户请求为：“{{request}}”.
            """)
    @Agent("对用户请求进行分类")
    CategoryRouter.RequestCategory classify(@MemoryId String memoryId, @V("request") String request);

}