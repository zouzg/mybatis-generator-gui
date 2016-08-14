package com.zzg.mybatis.generator.model;

/**
 * Created by Owen on 6/14/16.
 */
public enum DbType {

    MySQL("com.mysql.jdbc.Driver", "jdbc:mysql://%s:%s/%s?useUnicode=true&useSSL=false&characterEncoding=%s"),
    Oracle("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@%s:%s:%s"),
    PostgreSQL("org.postgresql.Driver", "jdbc:postgresql://%s:%s/%s");

    private final String driverClass;
    private final String connectionUrlPattern;

    DbType(String driverClass, String connectionUrlPattern) {
        this.driverClass = driverClass;
        this.connectionUrlPattern = connectionUrlPattern;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getConnectionUrlPattern() {
        return connectionUrlPattern;
    }

}