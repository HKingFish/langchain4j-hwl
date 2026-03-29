package com.kingfish.langchain4j.tools;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 改造版：支持客户端每次调用传递 JDBC 连接信息
 */
public class MySqlMcpDynamicTool {

    private static final Logger log = LoggerFactory.getLogger(MySqlMcpDynamicTool.class);
    private static final int MAX_POOL_SIZE = 10;
    private static final int MIN_IDLE_SIZE = 2;
    private static final long CONNECTION_TIMEOUT_MS = 30000L;

    // 缓存数据源，避免重复创建
    private final Map<String, HikariDataSource> dataSourceCache = new ConcurrentHashMap<>();

    // ======================== 工具方法：全部保留你的逻辑 ========================

    @Tool("执行 MySQL 查询语句，返回结构化结果。适用于 SELECT 类语句")
    public String executeQuery(
            @P("MySQL JDBC URL") String jdbcUrl,
            @P("数据库用户名") String username,
            @P("数据库密码") String password,
            @P("查询 SQL 语句") String sql
    ) {
        HikariDataSource dataSource = getDataSource(jdbcUrl, username, password);
        List<Map<String, Object>> resultList = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnLabel(i), rs.getObject(i));
                }
                resultList.add(row);
            }
            log.info("查询执行成功，返回 {} 条记录", resultList.size());
            return resultList.toString();

        } catch (SQLException e) {
            log.error("查询失败：{}", e.getMessage());
            return "查询失败：" + e.getMessage();
        }
    }

    @Tool("执行 MySQL 更新操作，返回受影响行数。适用于 INSERT / UPDATE / DELETE 类语句")
    public String executeUpdate(
            @P("MySQL JDBC URL") String jdbcUrl,
            @P("数据库用户名") String username,
            @P("数据库密码") String password,
            @P("更新 SQL 语句") String sql
    ) {
        HikariDataSource dataSource = getDataSource(jdbcUrl, username, password);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int affectedRows = pstmt.executeUpdate();
            log.info("更新执行成功，受影响行数：{}", affectedRows);
            return "执行成功，受影响行数：" + affectedRows;

        } catch (SQLException e) {
            log.error("更新失败：{}", e.getMessage());
            return "更新失败：" + e.getMessage();
        }
    }

    @Tool("获取当前数据库中所有表名，帮助了解数据库结构")
    public String listTables(
            @P("MySQL JDBC URL") String jdbcUrl,
            @P("数据库用户名") String username,
            @P("数据库密码") String password
    ) {
        HikariDataSource dataSource = getDataSource(jdbcUrl, username, password);
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
            log.error("获取表列表失败：{}", e.getMessage());
            return "获取表列表失败：" + e.getMessage();
        }
    }

    @Tool("获取指定表的字段结构信息，包含字段名、类型、是否可空等")
    public String describeTable(
            @P("MySQL JDBC URL") String jdbcUrl,
            @P("数据库用户名") String username,
            @P("数据库密码") String password,
            @P("表名") String tableName
    ) {
        HikariDataSource dataSource = getDataSource(jdbcUrl, username, password);
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
            log.error("获取表结构失败：{}", e.getMessage());
            return "获取表结构失败：" + e.getMessage();
        }
    }

    // ===================== 动态获取/创建数据源 =====================

    private HikariDataSource getDataSource(String jdbcUrl, String username, String password) {
        String key = jdbcUrl + "_" + username;
        // 原子性：确保只创建一次
        return dataSourceCache.computeIfAbsent(key, k -> {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setMaximumPoolSize(MAX_POOL_SIZE);
            config.setMinimumIdle(MIN_IDLE_SIZE);
            config.setConnectionTimeout(CONNECTION_TIMEOUT_MS);
            return new HikariDataSource(config);
        });
    }

    public void close() {
        dataSourceCache.values().forEach(HikariDataSource::close);
        log.info("所有数据源已关闭");
    }
}