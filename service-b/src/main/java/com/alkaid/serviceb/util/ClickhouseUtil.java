package com.alkaid.serviceb.util;

import com.alibaba.druid.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.clickhouse.ClickHouseConnection;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ClickhouseUtil {
    private static String url;
    private static String username;
    private static String password;
    private static String db;
    private static Integer socketTimeout;

    @Value("${spring.datasource.clickhouse.url}")
    public void setUrl(String url) {
        this.url = url;
    }

    @Value("${spring.datasource.clickhouse.username}")
    public void setUsername(String username) {
        this.username = username;
    }

    @Value("${spring.datasource.clickhouse.password}")
    public void setPassword(String password) {
        this.password = password;
    }

    @Value("${spring.datasource.clickhouse.db}")
    public void setDb(String db) {
        this.db = db;
    }

    @Value("${spring.datasource.clickhouse.socketTimeout}")
    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public static Connection getConnection() {
        ClickHouseConnection conn = null;
        ClickHouseProperties properties = new ClickHouseProperties();
        log.info(connString());
        if (!StringUtils.isEmpty(username)) {
            properties.setUser(username);
        }
        if (!StringUtils.isEmpty(password)) {
            properties.setPassword(password);
        }
        if (!StringUtils.isEmpty(db)) {
            properties.setDatabase(db);
        }
        properties.setSocketTimeout(socketTimeout);

        try {
            ClickHouseDataSource dataSource = new ClickHouseDataSource(url, properties);
            conn = dataSource.getConnection();
        } catch (SQLException se) {
            log.error(connString() + " Exception: ", se);
        } catch (Exception e) {
            log.error(connString() + " Exception: ", e);
        }
        return conn;
    }

    public static List<Map<String, Object>> sqlQuery(String sql) {
        return sqlQueryWithConnection(sql, getConnection());
    }

    public static List<Map<String, Object>> sqlQueryWithConnection(String sql, Connection connection) {
        log.info("Start " + sql);
        List<Map<String, Object>> data = new ArrayList<>();
        if (connection == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("Error: ", "Failedl to get Connection " + connString());
            data.add(error);
        } else {
            try {
                Statement statement = connection.createStatement();
                ResultSet results = statement.executeQuery(sql);
                ResultSetMetaData rsmd = results.getMetaData();
                while (results.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i=1; i <= rsmd.getColumnCount(); i++) {
                        row.put(rsmd.getColumnName(i), results.getString(i));
                    }
                    data.add(row);
                }
            } catch (SQLException e) {
                log.error(connString() + " [sql] " + " Exception: ", e);
                Map<String, Object> error = new HashMap<>();
                error.put("Error: ", e);
                data.add(error);
            }
        }

        return data;
    }

    public static String connString() {
        return "Connect: url: " + url + " username: " + username + " password: " + password + " db: " + db + " socketTimeout: " + socketTimeout;
    }
}