package com.kingfish.langchain4j;

import dev.langchain4j.community.mcp.server.McpServer;
import dev.langchain4j.community.mcp.server.transport.StdioMcpServerTransport;
import dev.langchain4j.mcp.protocol.McpImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * MySQL MCP 服务启动入口
 * 通过 Stdio 协议暴露 MySQL 操作工具，供 MCP 客户端（如 Kiro、Claude Desktop）调用
 */
public class McpLocalServerApplication {

    /** MCP Stdio 模式下，日志必须输出到 stderr，避免污染 stdout 的 JSON-RPC 通信流 */
    static {
        System.setProperty("org.slf4j.simpleLogger.logFile", "System.err");
    }

    private static final Logger log = LoggerFactory.getLogger(McpLocalServerApplication.class);

    /**
     * 命令行参数数量（jdbcUrl、username、password）
     */
    private static final int REQUIRED_ARGS_COUNT = 3;

    public static void main(String[] args) throws Exception {

        // 校验命令行参数
        if (args.length < REQUIRED_ARGS_COUNT) {
            System.err.println("参数错误！正确用法：java -jar mysql-mcp-server.jar <jdbcUrl> <username> <password>");
            System.err.println("示例：java -jar mysql-mcp-server.jar jdbc:mysql://localhost:3306/test_db root 123456");
            System.exit(1);
        }

        String jdbcUrl = args[0];
        String username = args[1];
        String password = args[2];

        log.info("正在启动 MySQL MCP Server...");

        // 配置 MCP 服务器元信息
        McpImplementation serverInfo = new McpImplementation();
        serverInfo.setName("mysql-mcp-server");
        serverInfo.setVersion("1.0.0");

        // 创建 MySQL 工具实例
        MySqlMcpTool mysqlTool = new MySqlMcpTool(jdbcUrl, username, password);

        // 注册关闭钩子，确保连接池资源释放
        Runtime.getRuntime().addShutdownHook(new Thread(mysqlTool::close));

        // 创建 MCP 服务器并注册工具
        McpServer server = new McpServer(List.of(mysqlTool), serverInfo);

        // 绑定 Stdio 传输层（核心通信通道）
        new StdioMcpServerTransport(System.in, System.out, server);

        log.info("MySQL MCP Server 已启动（Stdio 模式），连接地址：{}", jdbcUrl);

        // 阻塞主线程，保持服务器存活
        new CountDownLatch(1).await();
    }
}
