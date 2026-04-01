package com.kingfish.langchain4j;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MySQL 数据库操作工具类
 * 通过 MCP 协议暴露 SQL 查询与更新能力，供 LLM 调用
 */
@Slf4j
public class MySqlMcpTool {

    /**
     * 连接池最大连接数
     */
    private static final int MAX_POOL_SIZE = 10;

    /**
     * 连接池最小空闲连接数
     */
    private static final int MIN_IDLE_SIZE = 2;

    /**
     * 连接超时时间（毫秒）
     */
    private static final long CONNECTION_TIMEOUT_MS = 30000L;

    private final HikariDataSource dataSource;

    /**
     * 构造方法：初始化 HikariCP 数据库连接池
     *
     * @param jdbcUrl  MySQL 连接地址（如：jdbc:mysql://localhost:3306/test_db）
     * @param username 数据库用户名
     * @param password 数据库密码
     */
    public MySqlMcpTool(String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(MAX_POOL_SIZE);
        config.setMinimumIdle(MIN_IDLE_SIZE);
        config.setConnectionTimeout(CONNECTION_TIMEOUT_MS);
        this.dataSource = new HikariDataSource(config);
        log.info("MySQL 连接池初始化完成，地址：{}", jdbcUrl);
    }

    /**
     * 执行 SQL 查询（适用于 SELECT 类语句）
     *
     * @param sql 查询 SQL 语句（支持占位符 ?）
     * @return 查询结果（JSON 格式字符串，便于 LLM 解析）
     */
    @Tool("执行 MySQL 查询语句，返回结构化结果。适用于 SELECT 类语句")
    public String executeQuery(@P("查询 SQL 语句") String sql) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                // 使用 LinkedHashMap 保持列顺序
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnLabel(i), rs.getObject(i));
                }
                resultList.add(row);
            }

            log.info("查询执行成功，返回 {} 条记录", resultList.size());
            return resultList.toString();

        } catch (SQLException e) {
            log.error("MySQL 查询执行失败，SQL：{}，异常信息：{}", sql, e.getMessage());
            return "查询失败：" + e.getMessage();
        }
    }

    /**
     * 执行 SQL 更新操作（适用于 INSERT / UPDATE / DELETE 类语句）
     *
     * @param sql 更新 SQL 语句
     * @return 受影响的行数描述
     */
    @Tool("执行 MySQL 更新操作，返回受影响行数。适用于 INSERT / UPDATE / DELETE 类语句")
    public String executeUpdate(@P("更新 SQL 语句") String sql) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int affectedRows = pstmt.executeUpdate();
            log.info("更新执行成功，受影响行数：{}", affectedRows);
            return "执行成功，受影响行数：" + affectedRows;

        } catch (SQLException e) {
            log.error("MySQL 更新执行失败，SQL：{}，异常信息：{}", sql, e.getMessage());
            return "更新失败：" + e.getMessage();
        }
    }

    /**
     * 获取数据库中所有表名（辅助 LLM 了解数据库结构）
     *
     * @return 所有表名列表的字符串表示
     */
    @Tool("获取当前数据库中所有表名，帮助了解数据库结构")
    public String listTables() {
        List<String> tableNames = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             ResultSet rs = conn.getMetaData().getTables(
                     conn.getCatalog(), null, "%", new String[]{"TABLE"})) {

            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }

            log.info("获取表列表成功，共 {} 张表", tableNames.size());
            return "数据库表列表：" + tableNames;

        } catch (SQLException e) {
            log.error("获取表列表失败，异常信息：{}", e.getMessage());
            return "获取表列表失败：" + e.getMessage();
        }
    }

    /**
     * 获取指定表的字段结构信息
     *
     * @param tableName 表名
     * @return 表结构描述字符串
     */
    @Tool("获取指定表的字段结构信息，包含字段名、类型、是否可空等")
    public String describeTable(@P("表名") String tableName) {
        List<String> columns = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             ResultSet rs = conn.getMetaData().getColumns(
                     conn.getCatalog(), null, tableName, "%")) {

            while (rs.next()) {
                String columnInfo = rs.getString("COLUMN_NAME")
                        + " " + rs.getString("TYPE_NAME")
                        + "(" + rs.getInt("COLUMN_SIZE") + ")"
                        + (rs.getInt("NULLABLE") == 1 ? " NULL" : " NOT NULL");
                columns.add(columnInfo);
            }

            if (columns.isEmpty()) {
                return "未找到表 " + tableName + "，请检查表名是否正确";
            }

            log.info("获取表 {} 结构成功，共 {} 个字段", tableName, columns.size());
            return "表 " + tableName + " 结构：" + columns;

        } catch (SQLException e) {
            log.error("获取表 {} 结构失败，异常信息：{}", tableName, e.getMessage());
            return "获取表结构失败：" + e.getMessage();
        }
    }

    /**
     * 关闭连接池，释放资源
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("MySQL 连接池已关闭");
        }
    }
}
