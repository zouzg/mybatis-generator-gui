package com.zzg.mybatis.generator.model;

import java.util.Objects;

/**
 * Created by Owen on 5/13/16.
 */
public class DatabaseConfig {

	/**
	 * The primary key in the sqlite db
	 */
	private Integer id;

	private String dbType;
	/**
	 * The name of the config
	 */
	private String name;

	private String host;

	private String port;

	private String schema;

	private String username;

	private String password;

	private String encoding;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DatabaseConfig that = (DatabaseConfig) o;
		return Objects.equals(dbType, that.dbType) && Objects.equals(name, that.name) && Objects.equals(host, that
				.host) && Objects.equals(port, that.port) && Objects.equals(schema, that.schema) && Objects.equals
				(username, that.username) && Objects.equals(password, that.password) && Objects.equals(encoding, that
				.encoding);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dbType, name, host, port, schema, username, password, encoding);
	}

	@Override
	public String toString() {
		return "DatabaseConfig{" + "dbType='" + dbType + '\'' + ", name='" + name + '\'' + ", host='" + host + '\'' +
				", port='" + port + '\'' + ", schema='" + schema + '\'' + ", username='" + username + '\'' + ", " +
				"password='" + password + '\'' + ", encoding='" + encoding + '\'' + '}';
	}
}
