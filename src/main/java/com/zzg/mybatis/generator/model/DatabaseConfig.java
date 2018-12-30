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

    private String lport;

    private String rport;

    private String sshPort;

    private String sshHost;

    private String sshUser;

    private String sshPassword;

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

    public String getLport() {
        return lport;
    }

    public void setLport(String lport) {
        this.lport = lport;
    }

    public String getRport() {
        return rport;
    }

    public void setRport(String rport) {
        this.rport = rport;
    }

    public String getSshPort() {
        return sshPort;
    }

    public void setSshPort(String sshPort) {
        this.sshPort = sshPort;
    }

    public String getSshHost() {
        return sshHost;
    }

    public void setSshHost(String sshHost) {
        this.sshHost = sshHost;
    }

	public String getSshUser() {
		return sshUser;
	}

	public void setSshUser(String sshUser) {
		this.sshUser = sshUser;
	}

	public String getSshPassword() {
		return sshPassword;
	}

	public void setSshPassword(String sshPassword) {
		this.sshPassword = sshPassword;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DatabaseConfig that = (DatabaseConfig) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(dbType, that.dbType) &&
				Objects.equals(name, that.name) &&
				Objects.equals(host, that.host) &&
				Objects.equals(port, that.port) &&
				Objects.equals(schema, that.schema) &&
				Objects.equals(username, that.username) &&
				Objects.equals(password, that.password) &&
				Objects.equals(encoding, that.encoding) &&
				Objects.equals(lport, that.lport) &&
				Objects.equals(rport, that.rport) &&
				Objects.equals(sshPort, that.sshPort) &&
				Objects.equals(sshHost, that.sshHost) &&
				Objects.equals(sshUser, that.sshUser) &&
				Objects.equals(sshPassword, that.sshPassword);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, dbType, name, host, port, schema, username, password, encoding, lport, rport, sshPort, sshHost, sshUser, sshPassword);
	}

	@Override
	public String toString() {
		return "DatabaseConfig{" +
				"id=" + id +
				", dbType='" + dbType + '\'' +
				", name='" + name + '\'' +
				", host='" + host + '\'' +
				", port='" + port + '\'' +
				", schema='" + schema + '\'' +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", encoding='" + encoding + '\'' +
				", lport='" + lport + '\'' +
				", rport='" + rport + '\'' +
				", sshPort='" + sshPort + '\'' +
				", sshHost='" + sshHost + '\'' +
				", sshUser='" + sshUser + '\'' +
				", sshPassword='" + sshPassword + '\'' +
				'}';
	}
}
