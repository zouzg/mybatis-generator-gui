package com.zzg.mybatis.generator.util;

import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.model.DbType;
import com.zzg.mybatis.generator.model.UITableColumnVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Owen on 6/12/16.
 */
public class DbUtil {

    public static Connection getConnection(DatabaseConfig config) throws ClassNotFoundException, SQLException {
        DbType dbType = DbType.valueOf(config.getDbType());
        Class.forName(dbType.getDriverClass());
        return DriverManager.getConnection(getConnectionUrlWithoutSchema(config), config.getUsername(), config.getPassword());
    }

    public static List<String> getSchemas(DatabaseConfig config) throws Exception {
        DbType dbType = DbType.valueOf(config.getDbType());
        Class.forName(dbType.getDriverClass());
        Connection conn = DriverManager.getConnection(getConnectionUrlWithoutSchema(config), config.getUsername(), config.getPassword());
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getCatalogs();
        List<String> schemas = new ArrayList<>();
        while (rs.next()) {
            schemas.add(rs.getString("TABLE_CAT"));
        }
        return schemas;
    }

    public static List<String> getTableNames(DatabaseConfig config, String schema) throws Exception {
        DbType dbType = DbType.valueOf(config.getDbType());
        Class.forName(dbType.getDriverClass());
        Connection conn = DriverManager.getConnection(getConnectionUrlWithoutSchema(config), config.getUsername(), config.getPassword());
        conn.setSchema(schema);
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getTables(schema, null, null, null);
        List<String> tables = new ArrayList<>();
        while (rs.next()) {
            tables.add(rs.getString(3));
        }
        return tables;
    }

    public static List<UITableColumnVO> getTableColumns(DatabaseConfig dbConfig, String schema, String tableName) throws Exception {
        DbType dbType = DbType.valueOf(dbConfig.getDbType());
        Class.forName(dbType.getDriverClass());
        Connection conn = DriverManager.getConnection(getConnectionUrlWithoutSchema(dbConfig), dbConfig.getUsername(), dbConfig.getPassword());
        conn.setSchema(schema);
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getColumns(schema, null, tableName, null);
        List<UITableColumnVO> columns = new ArrayList<>();
        while (rs.next()) {
            UITableColumnVO columnVO = new UITableColumnVO();
            String columnName = rs.getString("COLUMN_NAME");
            columnVO.setColumnName(columnName);
            columnVO.setJdbcType(rs.getString("TYPE_NAME"));
            //columnVO.setPropertyName(StringUtils.dbStringToCamelStyle2(columnName));
            columns.add(columnVO);
        }
        return columns;
    }

    public static String getConnectionUrlWithoutSchema(DatabaseConfig dbConfig) {
        DbType dbType = DbType.valueOf(dbConfig.getDbType());
        String connectionUrl = String.format(dbType.getConnectionUrlPattern(), dbConfig.getHost(), dbConfig.getPort(), dbConfig.getEncoding());
        return connectionUrl;
    }

    public static String getConnectionUrlWithSchema(DatabaseConfig dbConfig) {
        DbType dbType = DbType.valueOf(dbConfig.getDbType());
        String connectionUrl = String.format(dbType.getFullConnectionUrlPattern(), dbConfig.getHost(), dbConfig.getPort(), dbConfig.getSchema(), dbConfig.getEncoding());
        return connectionUrl;
    }

}
