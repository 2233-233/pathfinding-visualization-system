package com.ldk.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest(classes = BackendApplication.class)  // 明确指定启动类
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("=== 数据库连接信息 ===");
            System.out.println("URL: " + conn.getMetaData().getURL());
            System.out.println("Database: " + conn.getCatalog());
            System.out.println("Driver: " + conn.getMetaData().getDriverName());
            System.out.println("Version: " + conn.getMetaData().getDatabaseProductVersion());
            
            // 查询当前数据库
            String dbName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            System.out.println("当前数据库: " + dbName);
            
            // 查询所有表
            System.out.println("\n=== 数据库中的表 ===");
            jdbcTemplate.query("SHOW TABLES", (rs, rowNum) -> {
                System.out.println("表名: " + rs.getString(1));
                return null;
            });
            
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}