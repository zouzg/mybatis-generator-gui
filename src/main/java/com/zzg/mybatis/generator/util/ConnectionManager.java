package com.zzg.mybatis.generator.util;

import com.alibaba.fastjson.JSON;
import com.zzg.mybatis.generator.model.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Owen on 8/21/16.
 */
public class ConnectionManager {

    private static final String DB_URL = "jdbc:sqlite:./config/sqlite3.db";

    public static Connection getConnection() throws Exception {
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection(DB_URL);
        return conn;
    }
}
