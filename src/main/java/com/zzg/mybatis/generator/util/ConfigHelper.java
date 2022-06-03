package com.zzg.mybatis.generator.util;

import com.alibaba.fastjson.JSON;
import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.model.DbType;
import com.zzg.mybatis.generator.model.GeneratorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * XML based config file help class
 * <p>
 * Created by Owen on 6/16/16.
 */
public class ConfigHelper {

	private static final Logger _LOG = LoggerFactory.getLogger(ConfigHelper.class);
	private static final String BASE_DIR = "config";
	private static final String CONFIG_FILE = "/sqlite3.db";

	public static void createEmptyFiles() throws Exception {
		File file = new File(BASE_DIR);
		if (!file.exists()) {
			file.mkdir();
		}
		File uiConfigFile = new File(BASE_DIR + CONFIG_FILE);
		if (!uiConfigFile.exists()) {
			createEmptyXMLFile(uiConfigFile);
		}
	}

	static void createEmptyXMLFile(File uiConfigFile) throws IOException {
		InputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("sqlite3.db");
			fos = new FileOutputStream(uiConfigFile);
			byte[] buffer = new byte[1024];
			int byteread = 0;
			while ((byteread = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, byteread);
			}
		} finally {
			if (fis != null) fis.close();
			if (fos != null) fos.close();
		}

	}

	public static List<DatabaseConfig> loadDatabaseConfig() throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery("SELECT * FROM dbs");
			List<DatabaseConfig> configs = new ArrayList<>();
			while (rs.next()) {
				int id = rs.getInt("id");
				String value = rs.getString("value");
				DatabaseConfig databaseConfig = JSON.parseObject(value, DatabaseConfig.class);
				databaseConfig.setId(id);
				configs.add(databaseConfig);
			}

			return configs;
		} finally {
			if (rs != null) rs.close();
			if (stat != null) stat.close();
			if (conn != null) conn.close();
		}
	}

	public static void saveDatabaseConfig(boolean isUpdate, Integer primaryKey, DatabaseConfig dbConfig) throws Exception {
		String configName = dbConfig.getName();
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			if (!isUpdate) {
				ResultSet rs1 = stat.executeQuery("SELECT * from dbs where name = '" + configName + "'");
				if (rs1.next()) {
					throw new RuntimeException("配置已经存在, 请使用其它名字");
				}
			}
			String jsonStr = JSON.toJSONString(dbConfig);
			String sql;
			if (isUpdate) {
				sql = String.format("UPDATE dbs SET name = '%s', value = '%s' where id = %d", configName, jsonStr, primaryKey);
			} else {
				sql = String.format("INSERT INTO dbs (name, value) values('%s', '%s')", configName, jsonStr);
			}
			stat.executeUpdate(sql);
		} finally {
			if (rs != null) rs.close();
			if (stat != null) stat.close();
			if (conn != null) conn.close();
		}
	}

	public static void deleteDatabaseConfig(DatabaseConfig databaseConfig) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			String sql = String.format("delete from dbs where id=%d", databaseConfig.getId());
			stat.executeUpdate(sql);
		} finally {
			if (rs != null) rs.close();
			if (stat != null) stat.close();
			if (conn != null) conn.close();
		}
	}

	public static void saveGeneratorConfig(GeneratorConfig generatorConfig) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			String jsonStr = JSON.toJSONString(generatorConfig);
			String sql = String.format("INSERT INTO generator_config values('%s', '%s')", generatorConfig.getName(),
					jsonStr);
			stat.executeUpdate(sql);
		} finally {
			if (rs != null) rs.close();
			if (stat != null) stat.close();
			if (conn != null) conn.close();
		}
	}

	public static GeneratorConfig loadGeneratorConfig(String name) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			String sql = String.format("SELECT * FROM generator_config where name='%s'", name);
			_LOG.info("sql: {}", sql);
			rs = stat.executeQuery(sql);
			GeneratorConfig generatorConfig = null;
			if (rs.next()) {
				String value = rs.getString("value");
				generatorConfig = JSON.parseObject(value, GeneratorConfig.class);
			}
			return generatorConfig;
		} finally {
			if (rs != null) rs.close();
			if (stat != null) stat.close();
			if (conn != null) conn.close();
		}
	}

	public static List<GeneratorConfig> loadGeneratorConfigs() throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			String sql = String.format("SELECT * FROM generator_config");
			_LOG.info("sql: {}", sql);
			rs = stat.executeQuery(sql);
			List<GeneratorConfig> configs = new ArrayList<>();
			while (rs.next()) {
				String value = rs.getString("value");
				configs.add(JSON.parseObject(value, GeneratorConfig.class));
			}
			return configs;
		} finally {
			if (rs != null) rs.close();
			if (stat != null) stat.close();
			if (conn != null) conn.close();
		}
	}

	public static int deleteGeneratorConfig(String name) throws Exception {
		Connection conn = null;
		Statement stat = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			String sql = String.format("DELETE FROM generator_config where name='%s'", name);
			_LOG.info("sql: {}", sql);
			return stat.executeUpdate(sql);
		} finally {
			if (stat != null) stat.close();
			if (conn != null) conn.close();
		}
	}

	public static String findConnectorLibPath(String dbType) {
		DbType type = DbType.valueOf(dbType);
		URL resource = Thread.currentThread().getContextClassLoader().getResource("logback.xml");
		_LOG.info("jar resource: {}", resource);
		if (resource != null) {
			try {
				File file = new File(resource.toURI().getRawPath() + "/../lib/" + type.getConnectorJarFile());
				return URLDecoder.decode(file.getCanonicalPath(), Charset.forName("UTF-8").displayName());
			} catch (Exception e) {
				throw new RuntimeException("找不到驱动文件，请联系开发者");
			}
		} else {
			throw new RuntimeException("lib can't find");
		}
	}

	public static List<String> getAllJDBCDriverJarPaths() {
		List<String> jarFilePathList = new ArrayList<>();
		URL url = Thread.currentThread().getContextClassLoader().getResource("logback.xml");
		try {
			File file;
			if (url.getPath().contains(".jar")) {
				file = new File("lib/");
			} else {
				file = new File("src/main/resources/lib");
			}
			_LOG.info("jar lib path: {}", file.getCanonicalPath());
			File[] jarFiles = file.listFiles();
			if (jarFiles != null && jarFiles.length > 0) {
				for (File jarFile : jarFiles) {
					_LOG.info("jar file: {}", jarFile.getAbsolutePath());
					if (jarFile.isFile() && jarFile.getAbsolutePath().endsWith(".jar")) {
						jarFilePathList.add(jarFile.getAbsolutePath());
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("找不到驱动文件，请联系开发者");
		}
		return jarFilePathList;
	}


}
