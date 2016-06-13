package com.zzg.mybatis.generator.model;

/**
 * Created by Owen on 5/13/16.
 */
public class DatabaseConfig {

    private DBType dbType;
    /**
     * The name of the config
     */
    private String name;

    private String host;

    private String port;


    private String username;

    private String password;

    private String encoding;

    public enum DBType {
        MYSQL("mysql", "com.mysql.jdbc.Driver", "jdbc:mysql://%s:%s?useUnicode=true&useSSL=false&characterEncoding=%s"),
        ORACLE("oracle", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@%s:%s");

        private final String name;
        private final String driverClass;
        private final String connectionUrlPattern;

        DBType(String name, String driverClass, String connectionUrlPattern) {
            this.name = name;
            this.driverClass = driverClass;
            this.connectionUrlPattern = connectionUrlPattern;
        }

        public String getDriverClass() {
            return driverClass;
        }
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

    public DBType getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = DBType.valueOf(dbType);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getConnectionUrl() {
        if (dbType == DBType.MYSQL) {
            return String.format(DBType.MYSQL.connectionUrlPattern, host, port, encoding);
        } else if (dbType == DBType.ORACLE) {
            return String.format(DBType.ORACLE.connectionUrlPattern, host, port);
        }
        return null;
    }
}
