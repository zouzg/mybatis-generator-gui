package com.zzg.mybatis.generator.model;

/**
 * Created by Owen on 6/14/16.
 */
public enum DbType {
    MYSQL("com.mysql.jdbc.Driver", "jdbc:mysql://%s:%s?useUnicode=true&useSSL=false&characterEncoding=%s", "jdbc:mysql://%s:%s/%s?useUnicode=true&useSSL=false&characterEncoding=%s"),
    ORACLE("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@%s:%s", "");

    private final String driverClass;
    private final String connectionUrlPattern;
    private final String fullConnectionUrlPattern;

    DbType(String driverClass, String connectionUrlPattern, String fullConnectionUrlPattern) {
        this.driverClass = driverClass;
        this.connectionUrlPattern = connectionUrlPattern;
        this.fullConnectionUrlPattern = fullConnectionUrlPattern;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getConnectionUrlPattern() {
        return connectionUrlPattern;
    }

    public String getFullConnectionUrlPattern() {
        return fullConnectionUrlPattern;
    }
}