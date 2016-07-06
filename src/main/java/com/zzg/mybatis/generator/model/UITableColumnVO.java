package com.zzg.mybatis.generator.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Owen on 6/22/16.
 */
public class UITableColumnVO {

    private BooleanProperty checked = new SimpleBooleanProperty(true); // Default set to true

    private StringProperty columnName = new SimpleStringProperty();

    private StringProperty javaType = new SimpleStringProperty();

    private StringProperty jdbcType = new SimpleStringProperty();

    private StringProperty propertyName = new SimpleStringProperty();

    private StringProperty typeHandle = new SimpleStringProperty();

    public String getColumnName() {
        return columnName.get();
    }

    public void setColumnName(String columnName) {
        this.columnName.set(columnName);
    }

    public String getJdbcType() {
        return jdbcType.get();
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType.set(jdbcType);
    }

    public String getPropertyName() {
        return propertyName.get();
    }

    public void setPropertyName(String propertyName) {
        this.propertyName.set(propertyName);
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public Boolean getChecked() {
        return this.checked.get();
    }

    public void setChecked(Boolean checked) {
        this.checked.set(checked);
    }

    public StringProperty typeHandleProperty() {
        return typeHandle;
    }

    public String getTypeHandle() {
        return typeHandle.get();
    }

    public void setTypeHandle(String typeHandle) {
        this.typeHandle.set(typeHandle);
    }

    public StringProperty columnNameProperty() {
        return columnName;
    }

    public StringProperty jdbcTypeProperty() {
        return jdbcType;
    }

    public StringProperty propertyNameProperty() {
        return propertyName;
    }

    public String getJavaType() {
        return javaType.get();
    }

    public StringProperty javaTypeProperty() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType.set(javaType);
    }
}
