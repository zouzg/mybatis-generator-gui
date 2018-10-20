package com.zzg.mybatis.generator.util;

import com.zzg.mybatis.generator.exception.DbDriverLoadingException;
import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.model.DbType;
import com.zzg.mybatis.generator.model.UITableColumnVO;
import org.mybatis.generator.internal.util.ClassloaderUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Created by Owen on 6/12/16.
 */
public class DbUtil {

    private static final Logger _LOG = LoggerFactory.getLogger(DbUtil.class);
    private static final int DB_CONNECTION_TIMEOUTS_SECONDS = 1;

    private static Map<DbType, Driver> drivers = new HashMap<>();

    public static Connection getConnection(DatabaseConfig config) throws ClassNotFoundException, SQLException {
		DbType dbType = DbType.valueOf(config.getDbType());
		if (drivers.get(dbType) == null){
			loadDbDriver(dbType);
		}

		String url = getConnectionUrlWithSchema(config);
	    Properties props = new Properties();

	    props.setProperty("user", config.getUsername()); //$NON-NLS-1$
	    props.setProperty("password", config.getPassword()); //$NON-NLS-1$

		DriverManager.setLoginTimeout(DB_CONNECTION_TIMEOUTS_SECONDS);
	    Connection connection = drivers.get(dbType).connect(url, props);
        _LOG.info("getConnection, connection url: {}", connection);
        return connection;
    }

    public static List<String> getTableNames(DatabaseConfig config) throws Exception {
        String url = getConnectionUrlWithSchema(config);
        _LOG.info("getTableNames, connection url: {}", url);
	    Connection connection = getConnection(config);
	    try {
		    List<String> tables = new ArrayList<>();
		    DatabaseMetaData md = connection.getMetaData();
		    ResultSet rs;
		    if (DbType.valueOf(config.getDbType()) == DbType.SQL_Server) {
			    String sql = "select name from sysobjects  where xtype='u' or xtype='v' ";
			    rs = connection.createStatement().executeQuery(sql);
			    while (rs.next()) {
				    tables.add(rs.getString("name"));
			    }
		    } else if (DbType.valueOf(config.getDbType()) == DbType.Oracle){
			    rs = md.getTables(null, config.getUsername().toUpperCase(), null, new String[] {"TABLE", "VIEW"});
		    } else if (DbType.valueOf(config.getDbType())==DbType.Sqlite){
		    	String sql = "Select name from sqlite_master;";
			    rs = connection.createStatement().executeQuery(sql);
			    while (rs.next()) {
				    tables.add(rs.getString("name"));
			    }
		    } 
		    else {
			    // rs = md.getTables(null, config.getUsername().toUpperCase(), null, null);


				rs = md.getTables(config.getSchema(), null, "%", new String[] {"TABLE", "VIEW"});			//针对 postgresql 的左侧数据表显示
		    }
		    while (rs.next()) {
				String schema = rs.getString(2) == null ? "" : rs.getString(2) + "."; //postgresql显示表时带上模式名，避免同一个数据库不同模式下的同一个表名左侧数据表显示时无法区分
				tables.add(schema + rs.getString(3));
			}
		    return tables;
	    } finally {
	    	connection.close();
	    }
	}

    public static List<UITableColumnVO> getTableColumns(DatabaseConfig dbConfig, String tableName) throws Exception {
        String url = getConnectionUrlWithSchema(dbConfig);
        _LOG.info("getTableColumns, connection url: {}", url);
		Connection conn = getConnection(dbConfig);
		try {
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
		} finally {
			conn.close();
		}
	}

    public static String getConnectionUrlWithSchema(DatabaseConfig dbConfig) throws ClassNotFoundException {
		DbType dbType = DbType.valueOf(dbConfig.getDbType());
		String connectionUrl = String.format(dbType.getConnectionUrlPattern(), dbConfig.getHost(), dbConfig.getPort(), dbConfig.getSchema(), dbConfig.getEncoding());
        _LOG.info("getConnectionUrlWithSchema, connection url: {}", connectionUrl);
        return connectionUrl;
    }

	/**
	 * 加载数据库驱动
	 * @param dbType 数据库类型
	 */
	private static void loadDbDriver(DbType dbType){
		List<String> driverJars = ConfigHelper.getAllJDBCDriverJarPaths();
		ClassLoader classloader = ClassloaderUtility.getCustomClassloader(driverJars);
		try {
			Class clazz = Class.forName(dbType.getDriverClass(), true, classloader);
			Driver driver = (Driver) clazz.newInstance();
			_LOG.info("load driver class: {}", driver);
			drivers.put(dbType, driver);
		} catch (Exception e) {
			_LOG.error("load driver error", e);
			throw new DbDriverLoadingException("找不到"+dbType.getConnectorJarFile()+"驱动");
		}
	}
}
