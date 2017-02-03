package com.zzg.mybatis.generator.util;

import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.model.DbType;
import com.zzg.mybatis.generator.model.UITableColumnVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Owen on 6/12/16.
 */
public class DbUtil {

    private static final Logger _LOG = LoggerFactory.getLogger(DbUtil.class);
    private static final int DB_CONNECTION_TIMEOUTS_SECONDS = 1;

    public static Connection getConnection(DatabaseConfig config) throws ClassNotFoundException, SQLException {
		DriverManager.setLoginTimeout(DB_CONNECTION_TIMEOUTS_SECONDS);
		DbType dbType = DbType.valueOf(config.getDbType());
		Class.forName(dbType.getDriverClass());
        String url = getConnectionUrlWithSchema(config);
        _LOG.info("getConnection, connection url: {}", url);
        return DriverManager.getConnection(url, config.getUsername(), config.getPassword());
    }

    public static List<String> getTableNames(DatabaseConfig config) throws Exception {
        String url = getConnectionUrlWithSchema(config);
        _LOG.info("getTableNames, connection url: {}", url);
        Connection conn = DriverManager.getConnection(url, config.getUsername(), config.getPassword());
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getTables(null, config.getUsername().toUpperCase(), null, null);
        List<String> tables = new ArrayList<>();
        while (rs.next()) {
            tables.add(rs.getString(3));
        }
        return tables;
    }

    public static List<UITableColumnVO> getTableColumns(DatabaseConfig dbConfig, String tableName) throws Exception {
        String url = getConnectionUrlWithSchema(dbConfig);
        _LOG.info("getTableColumns, connection url: {}", url);
		Connection conn = getConnection(dbConfig);
		DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getColumns(null, null, tableName, null);
        List<UITableColumnVO> columns = new ArrayList<>();
        while (rs.next()) {
            UITableColumnVO columnVO = new UITableColumnVO();
            String columnName = rs.getString("COLUMN_NAME");
            columnVO.setColumnName(columnName);
            columnVO.setJdbcType(rs.getString("TYPE_NAME"));
            columns.add(columnVO);
        }
        return columns;
    }

    public static String getConnectionUrlWithSchema(DatabaseConfig dbConfig) throws ClassNotFoundException {
		DbType dbType = DbType.valueOf(dbConfig.getDbType());
		String connectionUrl = String.format(dbType.getConnectionUrlPattern(), dbConfig.getHost(), dbConfig.getPort(), dbConfig.getSchema(), dbConfig.getEncoding());
        _LOG.info("getConnectionUrlWithSchema, connection url: {}", connectionUrl);
        return connectionUrl;
    }

}
