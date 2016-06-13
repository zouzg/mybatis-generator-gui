package com.zzg.mybatis.generator.util;

import com.zzg.mybatis.generator.model.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Owen on 6/12/16.
 */
public class DbUtil {

    public static Connection getConnection(DatabaseConfig config) throws ClassNotFoundException, SQLException {
        Class.forName(config.getDbType().getDriverClass());
        return DriverManager.getConnection(config.getConnectionUrl(), config.getUsername(), config.getPassword());
    }

    public static List<String> getSchemas(DatabaseConfig config) throws Exception {
        Class.forName(config.getDbType().getDriverClass());
        Connection conn = DriverManager.getConnection(config.getConnectionUrl(), config.getUsername(), config.getPassword());
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getCatalogs();
        List<String> schemas = new ArrayList<>();
        while (rs.next()) {
            schemas.add(rs.getString("TABLE_CAT"));
        }
        return schemas;
    }

    public static List<String> getTableNames(DatabaseConfig config) throws Exception {
        Class.forName(config.getDbType().getDriverClass());
        Connection conn = DriverManager.getConnection(config.getConnectionUrl(), config.getUsername(), config.getPassword());
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getTables(null, null, "%", null);
        List<String> tables = new ArrayList<>();
        while (rs.next()) {
            tables.add(rs.getString(3));
        }
        return tables;
    }

}
